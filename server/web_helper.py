#!/usr/bin/python2.7

import json
import logging
import urllib
import urllib2

UNKNOWN = (None, None, None, None)


def GetCityName(pos_x, pos_y):
    url = ('https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false' % (pos_y, pos_x))
    response = SendRequest(url)
    if not response:
        logging.error('Failed to connect to gmps')
        return UNKNOWN

    try:
        response = json.loads(response)
    except ValueError as e:
        logging.error('Failed to json decode response')
        return UNKNOWN

    if response['status'] != 'OK':
        logging.error('Status not ok')
        return UNKNOWN
    
    result = response['results'][0]
    city = None
    country = None
    street = None
    number = None
    for part in result['address_components']:
        if part['types'].count('country') > 0:
            country = part['long_name']
        elif part['types'].count('locality') > 0:
            city = part['long_name']
        elif part['types'].count('route') > 0:
            street = part['long_name']
        elif part['types'].count('street_number') > 0:
            number = part['long_name']

    return (city, country, street, number)


def SendRequest(url, request=None):
    try:
        if request:
            enc = urllib.urlencode(request)
            response = urllib2.urlopen(url, data=enc)
        else:
            response = urllib2.urlopen(url)
        return response.read()
    except urllib2.HTTPError as e:
        logging.error('Server error: ' + str(e))
        return None