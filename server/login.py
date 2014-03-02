#!/usr/bin/python2.7

import urllib
import urllib2

def Login(username, password):
    url='http://localhost:8080/login'
    request = {
        'username': username,
        'password': password
        }
    SendRequest(url, request)


def SendRequest(url, request):
    enc = urllib.urlencode(request)
    try:
        response = urllib2.urlopen(url, data=enc)
        print response.read()
    except urllib2.HTTPError as e:
        print 'Server error: ' + str(e)

Login('myuser', 'mypass')
Login('myuser', 'mypass3')
