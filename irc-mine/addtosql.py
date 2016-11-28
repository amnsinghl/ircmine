import MySQLdb
import re

db = MySQLdb.connect(host="localhost",    # your host, usually localhost
                     user="root",         # your username
                     passwd="aman",  # your password
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
def extract(limit, tableName):
    quer = "insert into " + tablename + " values ("
    content = f.readlines()
    for c in content:
        c = c.strip()
        chr = c.split(",")
        finalQuery = quer
        flag = False
        i = 0
        for cc in chr:
            if i == limit:
                break
            i = i + 1
            cc = cc.strip()
            if flag:
                finalQuery = finalQuery + ","
            flag = True
            try:
                x = int(cc)
                finalQuery = finalQuery + cc
            except:
                finalQuery = finalQuery + "\"" + re.escape(cc) + "\""
        finalQuery = finalQuery + ");"
        # print finalQuery
        cur.execute(finalQuery)


# query = "insert into channels values (\"hello\",2,\"kakak\");"
# cur.execute(query)
with open("channels") as f:
    limit = 3
    tablename = "channels"
    extract(limit, tablename)

db.commit()
db.close()