#!/usr/bin/python2.7


import constants
import db_helper
import json
import operator
import users


def ValidateArg(request, name, arg_type, remarks=None):
    result_str = request.get(name, None)
    if not result_str:
        raise HelperException('missing arg ' + name)
    if arg_type == 'float':
        try:
            result = float(result_str)
        except ValueError as e:
            raise HelperException('invalid value for ' + name + ' - ' + str(e))
    elif arg_type == 'int':
        try:
            result = int(result_str)
        except ValueError as e:
            raise HelperException('invalid value for ' + name + ' - ' + str(e))
        if remarks:
            if remarks == 'positive':
                if result <= 0:
                    raise HelperException('invalid value for arg ' + name +
                                          ' ' + str(result) + ' (remarks: ' +
                                          remarks + ')')
            else:
                raise HelperException('unknown remarks: ' + remarks)
    elif arg_type == 'str':
        if not isinstance(result_str, (str, unicode)):
            raise HelperException('invalid str type: ' + str(type(result_str)))
        result = result_str
    else:
        raise HelperException('unknwon arg type: ' + arg_type)

    return result


def FindClinics(pos_x, pos_y, min_results, max_result):
    rows = FindCloseClinics(pos_x, pos_y, min_results, max_result)
    result = []
    type_map = db_helper.GetNameMap()
    for row in rows:
        assert len(row) == len(type_map)
        resp = {}
        for i in range(len(row)):
            if row[i]:
                resp[type_map[i]] = row[i]
        result.append(resp)

    response = json.dumps(result)
    return response


def FindCloseClinics(pos_x, pos_y, min_results, max_results):
    # Try issuing queries with increasing block_size until a min number
    # of results are found or we exhaust our tries.
    block_size = constants.INIT_BLOCK_SIZE
    for _ in range(constants.MAX_POS_QUERY_RETRIES):
        db_req = ('POS_X >= %f AND POS_X <= %f AND POS_Y >= %f AND POS_Y <= %f' % 
                  (pos_x - block_size, pos_x + block_size,
                   pos_y - block_size, pos_y + block_size))
        try:
            rows = db_helper.QueryTable('clinics', db_req)
        except db_helper.DBException as e:
            raise HelperException(str(e))
        else:
            if len(rows) >= min_results:
                break
        block_size = block_size * constants.BLOCK_SIZE_GROW_FACTOR

    return SortByDistance(rows, pos_x, pos_y, max_results)


def SortByDistance(rows, pos_x, pos_y, max_results):
    dist_map = {}
    schema_map = db_helper.GetSchemaMap()
    count = 0
    for row in rows:
        x = row[schema_map['POS_X']]
        y = row[schema_map['POS_Y']]
        dist = (x - pos_x) * (x - pos_x) + (y - pos_y) * (y - pos_y)
        dist_map[count] = dist
        count += 1

    # Sort indices by distance.
    sorted_by_dist = sorted(dist_map.iteritems(), key=operator.itemgetter(1))

    result = []
    max_count = min(len(rows), max_results)
    for i in range(max_count):
        index = sorted_by_dist.pop(0)[0]
        result.append(rows[index])

    return result


def LoginOrCreateUser(username, password):
    if users.CheckUserExists(username):
        if not users.CheckUserAndPassword(username, password):
            raise HelperException('Invalid password')
    else:
        users.CreateUser(username, password)

    return users.GetUserSessionId(username)


def GetStatus(session_id):
    user = users.GetUserBySessionId(session_id)
    assert user
    username = users.GetUserName(user)


class HelperException(Exception):
    pass
