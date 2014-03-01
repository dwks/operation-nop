#!/usr/bin/python2.7

import webapp2

from paste import httpserver

import helper

MAIN_URL = '/main'
CLINIC_FINDER_URL = '/find_clinic'

class RedirectToMainHandler(webapp2.RequestHandler):
    def get(self):
        self.redirect(MAIN_URL, permanent=True)


class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('hello world')

class ClinicFinderHandler(webapp2.RequestHandler):
    def post(self):
        try:
            pos_x = helper.ValidateArg(self.request, 'pos_x', 'float')
            pos_y = helper.ValidateArg(self.request, 'pos_y', 'float')
            min_results = helper.ValidateArg(self.request, 'min_results', 'int', 'positive')
            max_results = helper.ValidateArg(self.request, 'max_results', 'int', 'positive')
        except helper.HelperException as e:
            self.response.out.write('Invalid request - ' + str(e))
            return
        self.response.out.write('success')


web_app = webapp2.WSGIApplication([
    ('/', RedirectToMainHandler),
    (MAIN_URL, MainHandler),
    (CLINIC_FINDER_URL, ClinicFinderHandler),
], debug=True)


def main():
    httpserver.serve(web_app, host='localhost', port='8080')


if __name__ == '__main__':
    main()
