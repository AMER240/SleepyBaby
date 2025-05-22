# Children's alarm clock project

# Create a database folder
import sqlite3
project=sqlite3.connect("DATABASE")
clock=project.cursor()

# Create a table and send it to the database
clock.execute("""CREATE TABLE IF NOT EXISTS 
              data(
              Name TEXT, 
              Age INTEGER,
              Gender TEXT,
              DailyLogOfHoursOfSleep INTEGER, 
              AverageNumberOfHoursOfSleepPerWeek INTEGER,
              AverageNumberOfHoursOfSleepPerMonth INTEGER
              )
              """)
liste=[
    ("Ahmed",1,"male",10,35,140)

]
clock.executemany("INSERT INTO data VALUES(?,?,?,?,?,?)",liste)

# Print table
inputs=clock.execute("SELECT rowid,* FROM data")
for row in inputs:
    print(row)

project.commit()
project.close()