#!/usr/bin/python2.7


import constants
import datetime
import db_helper
import json
import logging
import operator
import random
import users
import web_helper

UNKNOWN_THREE = (None, None, None)


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


def FindClosest(table_name,
                pos_x, pos_y,
                min_results, max_results,
                block_size, retries, block_growth):
    # Try issuing queries with increasing block_size until a min number
    # of results are found or we exhaust our tries.
    for _ in range(retries):
        db_req = ('POS_X >= %f AND POS_X <= %f AND POS_Y >= %f AND POS_Y <= %f' % 
                  (pos_x - block_size, pos_x + block_size,
                   pos_y - block_size, pos_y + block_size))
        try:
            rows = db_helper.QueryTable(table_name, db_req)
        except db_helper.DBException as e:
            raise HelperException(str(e))
        else:
            if len(rows) >= min_results:
                break
        block_size = block_size * block_growth

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


def GetStatus(session_id, pos_x, pos_y):
    user = users.GetUserBySessionId(session_id)
    assert user
    username = users.GetUserName(user)

    response = {
        'status': random.randint(0, 10)
        }

    # Try get location info.
    city, country, street, number = web_helper.GetCityName(pos_x, pos_y)

    if city:
        response['city'] = city
    if country:
        response['country'] = country
    if city:
        response['street'] = street
    if city:
        response['number'] = number

    # Try get air info.
    air_quality_sites = GetAirQualitySite(pos_x, pos_y)
    schema_map = db_helper.GetSchemaMap()

    air_quality = 0
    sample_count = 0
    for air_quality_site in air_quality_sites:
        sample = web_helper.GetAirQuality(
            air_quality_site[schema_map['RES2']])
        if sample:
            air_quality += sample
            sample_count += 1.0

    if sample_count:
        response['air_quality'] = air_quality / sample_count
        logging.info('Air quality: ' + str(response['air_quality']))

    # Try get flu info.
    flu_people, flu_hospitals, flu_work_places = GetFluInfo()

    if flu_people:
        response['flu_people'] = flu_people
        
    if flu_hospitals:
        response['flu_hospitals'] = flu_hospitals

    if flu_work_places:
        response['flu_work_places'] = flu_work_places

    return json.dumps(response)


def GetAirQualitySite(pos_x, pos_y):
    result = FindClosest(
        'air_quality_sites', pos_y, pos_x, 1, 2, 0.2, 5, 5)
    assert result
    return result

def GetFluInfo():
    # For flu info:
    # RES0: week number in the year
    # VALUE: value of data
    # MIN: min value diff in year
    # MAX: max value diff in year
    # RES1: max value in year
    schema_map = db_helper.GetSchemaMap()

    date = datetime.datetime.now()
    week_num = date.isocalendar()[1]
    last_week_num = (week_num + 51) % 52

    response = []
    week_nums = [week_num, last_week_num]
    db_names = ['flu_sick_people', 'flu_sick_hospitals', 'flu_sick_work_places']
    for db_name in db_names:
        week_data = []
        for week in week_nums:
            db_req = 'RES0 = ' + str(week)
            rows = db_helper.QueryTable(db_name, db_req)
            if not len(rows) == 1:
                logging.error(db_name + ' DB in bad state - returned %d for week ' % len(rows) + str(week))
                return UNKNOWN_THREE

            value = rows[0][schema_map['VALUE']]
            week_data.append(value)
            max_value = float(rows[0][schema_map['RES1']])
            min_diff = rows[0][schema_map['MIN']]
            max_diff = rows[0][schema_map['MAX']]
            week_maxes = (max_value, min_diff, max_diff)

        week_quality = 10 * (1 - week_data[0] / week_maxes[0])
        week_percent = 100 * ((week_data[0] / week_data[1]) - 1)

        week_diff = week_data[0] - week_data[1]
        week_diff_quality = 10 * (week_maxes[2] - week_diff) / (week_maxes[2] - week_maxes[1]) 

        response.append((week_quality, week_diff_quality, week_percent))

    return response



class HelperException(Exception):
    pass
