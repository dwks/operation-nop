#!/usr/bin/python2.7

import optparse
import webapp2

from paste import httpserver

import db_helper


CREATE_TABLE_URL = '/create_table'
DELETE_TABLE_URL = '/delete_table'
INSERT_DATA_URL = '/insert_data'

class CreateTableHandler(webapp2.RequestHandler):
    def get(self):
        table_name = self.request.get('table_name', None)
        if not table_name:
            self.response.out.write('Invalid missing table_name')
            return
        try:
            db_helper.CreateTable(table_name)
        except db_helper.DBException as e:
            self.response.out.write('DB error - ' + str(e))
        else:
            self.response.out.write('Success!')


class DeleteTableHandler(webapp2.RequestHandler):
    def get(self):
        table_name = self.request.get('table_name', None)
        if not table_name:
            self.response.out.write('Invalid missing table_name')
            return
        try:
            db_helper.DeleteTable(table_name)
        except db_helper.DBException as e:
            self.response.out.write('DB error - ' + str(e))
        else:
            self.response.out.write('Success!')


class InsertDataHandler(webapp2.RequestHandler):
    def post(self):
        table_name = self.request.get('table_name', None)
        if not table_name:
            self.response.out.write('Invalid missing table_name')
            return
        data = self.request.get('data', None)
        if not data:
            self.response.out.write('Invalid missing data')
            return
        try:
            db_helper.InsertData(table_name, data)
        except db_helper.DBException as e:
            self.response.out.write('DB error - ' + str(e))
        else:
            self.response.out.write('Success!')


web_app = webapp2.WSGIApplication([
    (CREATE_TABLE_URL, CreateTableHandler),
    (DELETE_TABLE_URL, DeleteTableHandler),
    (INSERT_DATA_URL, InsertDataHandler),
], debug=True)


def main():
    parser = optparse.OptionParser()
    parser.add_option('-a', '--address', default='localhost',
                      help='Address of the server. '
                      'Defaults to %default. ')
    parser.add_option('-p', '--port', default='9090', type='int',
                      help='Port of the server. '
                      'Defaults to %default.')
    parser.add_option('-v', '--verbose', action='store_true')
    (options, args) = parser.parse_args()

    httpserver.serve(web_app, host=options.address, port=options.port)


if __name__ == '__main__':
    main()
