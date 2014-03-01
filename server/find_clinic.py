#!/usr/bin/python2.7

import urllib
import urllib2

url='http://localhost:8080/find_clinic'

request = {
    'pos_x': '1.0',
    'pos_y': '-1.0',
    'min_results': '1',
    'max_results': '10'
    }

enc = urllib.urlencode(request)
response = urllib2.urlopen(url, data=enc)
print response.read()

