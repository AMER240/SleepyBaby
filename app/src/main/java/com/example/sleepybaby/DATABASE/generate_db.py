import sqlite3

conn = sqlite3.connect("sleepyBaby.db")
cursor = conn.cursor()

cursor.execute("""
CREATE TABLE IF NOT EXISTS children (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    birthDate INTEGER NOT NULL,
    gender TEXT NOT NULL,
    sleepHour INTEGER NOT NULL,
    sleepMinute INTEGER NOT NULL,
    wakeHour INTEGER NOT NULL,
    wakeMinute INTEGER NOT NULL
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS sleep_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    childId INTEGER NOT NULL,
    startTime INTEGER,
    endTime INTEGER,
    quality INTEGER,
    notes TEXT,
    FOREIGN KEY (childId) REFERENCES children(id)
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS sleep_statistics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    childId INTEGER NOT NULL,
    date INTEGER,
    totalSleepMinutes INTEGER,
    numberOfSleeps INTEGER,
    averageSleepQuality REAL,
    longestSleepMinutes INTEGER,
    shortestSleepMinutes INTEGER,
    FOREIGN KEY (childId) REFERENCES children(id)
)
""")

conn.commit()
conn.close()

print("The sleepyBaby.db database was created successfully.")
