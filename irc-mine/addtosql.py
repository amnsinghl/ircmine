import MySQLdb
import re

db = MySQLdb.connect(host="localhost",    # your host, usually localhost
                     user="root",         # your username
                     db="irc")        # name of the data base

# you must create a Cursor object. It will let
#  you execute all the queries you need
cur = db.cursor()

# Use all the SQL you like
# cur.execute("SELECT * FROM channels")
#
# # print all the first cell of all the rows
# for row in cur.fetchall():
#     print row[0]
def extract(limit, tableName, prefix):
    quer = "insert into " + tablename + " values (\"" + prefix + "\""
    content = f.readlines()
    for c in content:
        c = c.strip()
        chr = c.split(",")
        finalQuery = quer
        i = 0
        for cc in chr:
            if i == limit:
                break
            i = i + 1
            cc = cc.strip()
            finalQuery = finalQuery + ","
            if cc.replace('.','',1).isdigit():
                finalQuery = finalQuery + cc
            else:
                finalQuery = finalQuery + "\"" + re.escape(cc) + "\""
        finalQuery = finalQuery + ");"
        # print finalQuery
        try:
            cur.execute(finalQuery)
        except:
            a = 1
# query = "insert into channels values (\"hello\",2,\"kakak\");"
# cur.execute(query)
prefix = "rizon"
with open("channels") as f:
    limit = 3
    tablename = "channels"
#    extract(limit, tablename, prefix)
db.commit()
print "done"

with open("memberDataUnique") as f:
    limit = 4
    tablename = "memberdata"
    extract(limit, tablename, prefix)
db.commit()
print "done"

with open("channelMembers") as f:
    limit = 2
    tablename = "channelmembers"
    extract(limit, tablename, prefix)
db.commit()
print "done"

with open("memberLocation") as f:
    limit = 8
    tablename = "memberlocation"
    extract(limit, tablename, prefix)
print "done"

db.commit()
db.close()
