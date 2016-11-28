import MySQLdb

db = MySQLdb.connect(host="localhost",    # your host, usually localhost
                     user="root",         # your username
                     db="irc")        # name of the data base

cur = db.cursor()

cur.execute("create table channels (network varchar(50), name varchar(100), membercount integer, topic varchar(1000));")
cur.execute("create table memberdata (network varchar(50), nick varchar(100), host varchar(100), realname varchar(100), server varchar(100));")
cur.execute("create table channelmembers (network varchar(50), channelname varchar(100),  nick varchar(100));")
cur.execute("create table memberlocation (network varchar(50), nick varchar(100),  countryCode varchar(100), region varchar(100), country varchar(100), regionName varchar(100), city varchar(100), lat double, lon double);")

db.commit()
db.close()