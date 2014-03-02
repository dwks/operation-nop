#!/usr/bin/python2.7

import sys
import urllib
import urllib2

server = 'http://sirius.nss.cs.ubc.ca:9090'

def InsertData(table_name):
    url=server + '/insert_data'

    if len(sys.argv) > 1:
        filename = sys.argv[1]
        file_p = open(filename)
        data = file_p.read()
        file_p.close()
    else:
        sys.exit(1)

    request = {
        'table_name': table_name,
        'data': data
        }
    SendRequest(url, request)

def SendRequest(url, request):
    if request:
        enc = urllib.urlencode(request)
        response = urllib2.urlopen(url, data=enc)
    else:
        response = urllib2.urlopen(url)
    print response.read()


def CreateTable(table_name):
    url=server + '/create_table?table_name=' + table_name
    SendRequest(url, None)


def DeleteTable(table_name):
    url=server+'/delete_table?table_name=' + table_name
    SendRequest(url, None)


def main():
    CreateTable('air_quality_sites')
    InsertData('air_quality_sites')


if __name__ == '__main__':
    main()

