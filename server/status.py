#!/usr/bin/python2.7

import urllib
import urllib2

def Status(session_id):
    url='http://localhost:8080/status'
    request = {
        'session_id': session_id
        }
    SendRequest(url, request)


def SendRequest(url, request):
    enc = urllib.urlencode(request)
    try:
        response = urllib2.urlopen(url, data=enc)
        print response.read()
    except urllib2.HTTPError as e:
        print 'Server error: ' + str(e)

Status('1123')
