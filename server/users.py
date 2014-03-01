#!/usr/bin/python2.7

import db_helper
import uuid

# For users:
# RES2: username
# RES3: pass
# TIME: uuid

TABLE_NAME = 'operation_nop_users'


def CreateUser(username, password):
    data = 'RES2|RES3|TIME\n' + username + '|' + password + '|' + str(uuid.uuid4())
    db_helper.InsertData(TABLE_NAME, data)
    db_helper.CreateTable(TABLE_NAME + '_' + username)


def CheckUserSessionId(username, session_id):
    pass


def GetUserSessionId(username):
    user = GetUser(username)
    assert user

    schema_map = db_helper.GetSchemaMap()
    return user[schema_map['TIME']]


# Returns True if user exists and matches password.
def CheckUserAndPassword(username, password):
    user = GetUser(username)
    schema_map = db_helper.GetSchemaMap()
    if user[schema_map['RES3']] == password:
        return True

    return False


# Returns True if user exists.
def CheckUserExists(username):
    user = GetUser(username)
    if user:
        return True
    return False


def GetUser(username):
    db_req = ("RES2 == '" + username + "'")
    try:
        rows = db_helper.QueryTable(TABLE_NAME, db_req)
    except db_helper.DBException as e:
        return False
    if len(rows) > 1:
        error = 'Users DB in bad shape - %d users named: ' % len(rows) + username
        logging.error(error)
        raise UserException(error)
    elif len(rows) == 1:
        return rows[0]
    
    return None


class UsersException(Exception):
    pass
