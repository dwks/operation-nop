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
    data = 'POS_X|POS_Y\n1|1\n10|10'

request = {
    'table_name': 'clinics',
    'data': data
    }

enc = urllib.urlencode(request)
response = urllib2.urlopen(url, data=enc)
print response.read()

