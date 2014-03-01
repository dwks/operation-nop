#!/usr/bin/python2.7

import db_helper


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


def FindClinics(pos_x, pos_y, block_size, min_results, max_result):
    db_req = ('POS_X >= %f AND POS_Y <= %f AND POS_Y >= %f AND POS_Y <= %f' % 
             (pos_x - block_size, pos_x + block_size,
              pos_y - block_size, pos_y + block_size))
    # try:
    #     resp = db_helper.QueryTable('clinics', db_req)
    # except db_helper.DBException as e:
    #     raise HelperException(str(e))
    # return
    # for line in resp:
    #     print line
    return 'success'


class HelperException(Exception):
    pass
