#!/usr/bin/python2.7

# from db import db_helper


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
    return 'success'


class HelperException(Exception):
    pass
