#!/usr/bin/python2.7

import webapp2

from paste import httpserver

MAIN_URL = '/main'

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('hello world')


web_app = webapp2.WSGIApplication([
    (MAIN_URL, MainHandler),
], debug=True)


def main():
    httpserver.serve(web_app, host='localhost', port='8080')


if __name__ == '__main__':
    main()
