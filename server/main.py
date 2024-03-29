#!/usr/bin/python2.7

import optparse
import webapp2

from paste import httpserver

import logging
import main_helper

MAIN_URL = '/main'
CLINIC_FINDER_URL = '/find_clinic'
LOGIN_URL = '/login'
STATUS_URL = '/status'


class RedirectToMainHandler(webapp2.RequestHandler):
    def get(self):
        self.redirect(MAIN_URL, permanent=True)


class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('hello world')
        self.response.status = 200

    def post(self):
        self.response.write('hello world')
        self.response.status = 200


class ClinicFinderHandler(webapp2.RequestHandler):
    def post(self):
        try:
            pos_x = main_helper.ValidateArg(self.request, 'pos_x', 'float')
            pos_y = main_helper.ValidateArg(self.request, 'pos_y', 'float')
            min_results = main_helper.ValidateArg(
                self.request, 'min_results', 'int', 'positive')
            max_results = main_helper.ValidateArg(
                self.request, 'max_results', 'int', 'positive')
        except main_helper.HelperException as e:
            self.response.out.write('Invalid request - ' + str(e))
            self.response.status = 400
            return

        try:
            result = main_helper.FindClinics(
                pos_x, pos_y, min_results, max_results)
        except main_helper.HelperException as e:
            logging.error('Internal error :( - ' + str(e))
            self.response.status = 500
        else:
            self.response.out.write(result)

class LoginHandler(webapp2.RequestHandler):
    def post(self):
        try:
            username = main_helper.ValidateArg(self.request, 'username', 'str')
            password = main_helper.ValidateArg(self.request, 'password', 'str')
        except main_helper.HelperException as e:
            self.response.out.write('Invalid request - ' + str(e))
            self.response.status = 400
            return

        try:
            response = main_helper.LoginOrCreateUser(username, password)
            self.response.out.write(response)
        except main_helper.HelperException as e:
            self.response.status = 400


class StatusHandler(webapp2.RequestHandler):
    def post(self):
        try:
            session_id = main_helper.ValidateArg(self.request, 'session_id', 'str')
            pos_x = main_helper.ValidateArg(self.request, 'pos_x', 'float')
            pos_y = main_helper.ValidateArg(self.request, 'pos_y', 'float')
        except main_helper.HelperException as e:
            self.response.out.write('Invalid request - ' + str(e))
            self.response.status = 400
            return

        logging.info('Recv status request from: ' + self.request.remote_addr)
        response = main_helper.GetStatus(session_id, pos_x, pos_y)
        if response:
            logging.info('Response to status: ' + str(response))
            self.response.out.write(response)    


web_app = webapp2.WSGIApplication([
    ('/', RedirectToMainHandler),
    (MAIN_URL, MainHandler),
    (CLINIC_FINDER_URL, ClinicFinderHandler),
    (LOGIN_URL, LoginHandler),
    (STATUS_URL, StatusHandler),
], debug=True)


def main():
    parser = optparse.OptionParser()
    parser.add_option('-a', '--address', default='localhost',
                      help='Address of the server. '
                      'Defaults to %default. ')
    parser.add_option('-p', '--port', default='8080', type='int',
                      help='Port of the server. '
                      'Defaults to %default.')
    parser.add_option('-v', '--verbose', action='store_true')
    (options, args) = parser.parse_args()
    if options.verbose:
        logging.root.setLevel(logging.INFO)

    httpserver.serve(web_app, host=options.address, port=options.port)

if __name__ == '__main__':
    main()
