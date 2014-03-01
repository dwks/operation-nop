#!/usr/bin/python2.7

def ValidateArg(request, name, argType, remarks=None):
    result_str = request.get(name, None)
    if not result_str:
        raise HelperException('missing arg ' + name)
    if argType == 'float':
        result = float(result_str)
    elif argType == 'int':
        result = int(result_str)
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


class HelperException(Exception):
    pass
