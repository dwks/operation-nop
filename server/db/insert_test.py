#!/usr/bin/python2.7

import urllib
import urllib2

url='http://localhost:8080/insert_data'

request = {
    'table_name': 'data',
    'data': 'POS_X|POS_Y\n123|456'
    }

enc = urllib.urlencode(request)
response = urllib2.urlopen(url, data=enc)
print response.read()

