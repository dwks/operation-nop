#!/usr/bin/python2.7


import constants
import db_helper
import json


def ValidateArg(request, name, argType, remarks=None):
    result_str = request.get(name, None)
    if not result_str:
        raise HelperException('missing arg ' + name)
    if argType == 'float':
        try:
            result = float(result_str)
        except ValueError as e:
            raise HelperException('invalid value for ' + name + ' - ' + str(e))
    elif argType == 'int':
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
    else:
        raise HelperException('unknwon arg type: ' + argType)

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


def FindCloseClinics(pos_x, pos_y, min_results, max_result):
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
                return rows
        block_size = block_size * constants.BLOCK_SIZE_GROW_FACTOR

    return rows


class HelperException(Exception):
    pass
