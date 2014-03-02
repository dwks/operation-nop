#!/usr/bin/python2.7

import urllib
import urllib2

def Status(session_id):
    url='http://localhost:8080/status'
    request = {
        'session_id': session_id,
        'pos_x': -123.24853,
        'pos_y': 49.26135
        # 'pos_x': -123,
        # 'pos_y': 49
        }
    SendRequest(url, request)


def SendRequest(url, request):
    enc = urllib.urlencode(request)
    try:
        response = urllib2.urlopen(url, data=enc)
        print response.read()
    except urllib2.HTTPError as e:
        print 'Server error: ' + str(e)

Status('f55c5204-2980-4f3c-ba2e-8a0bbc340d3c')
