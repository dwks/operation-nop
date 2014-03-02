#!/usr/bin/python2.7

import json
import logging
import os
import subprocess
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
        logging.error('Status not ok for (%f, %f)' % (pos_x, pos_y))
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
        if request != None:
            enc = urllib.urlencode(request)
            response = urllib2.urlopen(url, data=enc)
        else:
            response = urllib2.urlopen(url)
        return response.read()
    except urllib2.HTTPError as e:
        logging.error('Server error: ' + str(e))
        return None


def GetAirQuality(air_quality_site):
    command = ['perl', 'aqicn.pl', air_quality_site + 'm/']
    try:
        air_quality_str = subprocess.check_output(command, stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e:
        logging.error('Executing aqicn.pl failed: ' + e.output)
        return None
    except OSError as e:
        logging.error('Executing aqicn.pl failed: ' + str(e))
        return None

    try:
        air_quality = float(air_quality_str)
    except ValueError:
        logging.error('Failed to parse float of air quality from: ' + air_quality_str)
        return None

    air_quality = 10 * (1 - min(air_quality, 300) / 300.0)

    return air_quality
