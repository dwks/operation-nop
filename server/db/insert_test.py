#!/usr/bin/python2.7

import sys
import urllib
import urllib2

url='http://localhost:8080/insert_data'

if len(sys.argv) > 1:
    filename = sys.argv[1]
    file_p = open(filename)
    data = file_p.read()
    file_p.close()
else:
    data = 'POS_X|POS_Y\n111|112\n222|222\n333|332\n444|442'

request = {
    'table_name': 'temperature',
    'data': data
    }

enc = urllib.urlencode(request)
response = urllib2.urlopen(url, data=enc)
print response.read()

