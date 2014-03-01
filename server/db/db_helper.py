#!/usr/bin/python2.7

import sqlite3


DB_NAME = 'test.db'
COLUMN_FORMAT = '(Id INT, TIME TEXT, POS_X REAL, POS_Y REAL, VALUE REAL, MIN REAL, MAX REAL, DESC TEXT, RES0 REAL, RES1 REAL, RES2 TEXT, RES3 TEXT)'

def CreateTable(table_name):
    con = sqlite3.connect(DB_NAME)
    with con:
        try:
            con.execute('CREATE TABLE ' + table_name + COLUMN_FORMAT)
        except sqlite3.OperationalError as e:
            raise DBException(str(e))


def DeleteTable(table_name):
    con = sqlite3.connect(DB_NAME)
    with con:
        try:
            con.execute('DROP TABLE ' + table_name)
        except sqlite3.OperationalError as e:
            raise DBException(str(e))


class DBException(Exception):
    pass
