#!/usr/bin/python2.7

import urllib
import urllib2

url='http://localhost:8080/find_clinic'

request = {
    'pos_x': '3.0',
    'pos_y': '2.0',
    'min_results': '2',
    'max_results': '20'
    }

enc = urllib.urlencode(request)
try:
    response = urllib2.urlopen(url, data=enc)
    print response.read()
except urllib2.HTTPError as e:
    print 'Server error: ' + str(e)
