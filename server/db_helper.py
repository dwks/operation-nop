#!/usr/bin/python2.7

import logging
import sqlite3


DB_NAME = '../../hackathon_data/db/data.db'
COLUMN_FORMAT = '(ID INT, TIME TEXT, POS_X REAL, POS_Y REAL, VALUE REAL, MIN REAL, MAX REAL, DESC TEXT, RES0 REAL, RES1 REAL, RES2 TEXT, RES3 TEXT)'


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


def InsertData(table_name, data):
    lines = data.split('\n')
    if not len(lines):
        raise DBException('Invalid data lines')

    header = lines[0]
    lines.pop(0)

    schema_map = _GetSchemaMap()
    index_list = _GetIndexList(header, schema_map)

    con = sqlite3.connect(DB_NAME)
    with con:
        for line in lines:
            if not line:
                continue
            fmt_line = _FormatLine(line, index_list)
            try:
                db_req = 'INSERT INTO ' + table_name + ' VALUES(' + fmt_line + ')'
                con.execute(db_req)
            except sqlite3.OperationalError as e:
                raise DBException('data insert error - ' + str(e) + ' on line: ' + db_req)


def _FormatLine(line, index_list):
    type_map = GetTypeMap()
    parts = line.split('|')
    if len(parts) != len(index_list):
        raise DBException('Invalid data line: [' + line + ']')

    result = {}
    for i in range(len(type_map.keys())):
        result[i] = None

    for index in index_list:
        value = parts.pop(0)
        result[index] = _GetFormattedValue(value, type_map[index])
    
    for i in range(len(type_map.keys())):
        if result[i] == None:
            result[i] = _GetDefaultValue(type_map[i])

    fmt_result = ''
    keys = result.keys()
    for key in keys:
        fmt_result += result[key] + ', '

    return fmt_result[:-2]


def _GetDefaultValue(type_name):
    if type_name == 'INT':
        return 'NULL'
    elif type_name == 'TEXT':
        return 'NULL'
    elif type_name == 'REAL':
        return 'NULL'
    
    raise DBException('Invalid data type:' + type_name)

def _GetFormattedValue(value, type_name):
    if type_name == 'INT':
        return value
    elif type_name == 'TEXT':
        return "'" + value + "'"
    elif type_name == 'REAL':
        return value
    
    raise DBException('Invalid data type:' + type_name)

def _GetIndexList(header, schema_map):
    result = []

    parts = header.split('|')
    for part in parts:
        if part not in schema_map:
            raise DBException('Invalid ' + part + ' not in schema')
        result.append(schema_map[part])

    return result


def _GetSchemaMap():
    result = {}
    parts = COLUMN_FORMAT[1:-1].split(',')
    count = 0
    for part in parts:
        result[part.split()[0]] = count
        count += 1

    return result


def GetTypeMap():
    result = {}
    parts = COLUMN_FORMAT[1:-1].split(',')
    count = 0
    for part in parts:
        result[count] = part.split()[1]
        count += 1

    return result


def GetNameMap():
    result = {}
    parts = COLUMN_FORMAT[1:-1].split(',')
    count = 0
    for part in parts:
        result[count] = part.split()[0]
        count += 1

    return result


def QueryTable(table_name, query_string):
    con = sqlite3.connect(DB_NAME)
    with con:
        try:
            cur = con.cursor()
            db_req = 'SELECT * FROM ' + table_name + ' WHERE ' + query_string
            logging.info('Executing query on table [' + table_name + ']: ' + db_req)
            cur.execute(db_req)
        except sqlite3.OperationalError as e:
            raise DBException('error executing query - ' + str(e) + ' [' + db_req + ']')
        else:
            rows = cur.fetchall()
            logging.info('Query returned %d results' % len(rows))
            return rows


class DBException(Exception):
    pass
