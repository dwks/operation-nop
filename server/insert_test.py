#!/usr/bin/python2.7

import sys
import urllib
import urllib2

def InsertData(table_name):
    url='http://localhost:8080/insert_data'

    if len(sys.argv) > 1:
        filename = sys.argv[1]
        file_p = open(filename)
        data = file_p.read()
        file_p.close()
    else:
        data = 'POS_X|POS_Y\n1|1\n10|10'

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
    url='http://localhost:8080/create_table?table_name=' + table_name
    SendRequest(url, None)


def DeleteTable(table_name):
    url='http://localhost:8080/delete_table?table_name=' + table_name
    SendRequest(url, None)


def main():
    CreateTable('test')
    CreateTable('test')
    InsertData('test')
    DeleteTable('test')
    DeleteTable('test')


if __name__ == '__main__':
    main()

