import sqlite3
import os

def create_database():
    try:
        # حذف قاعدة البيانات القديمة إذا كانت موجودة
        if os.path.exists("sleepyBaby.db"):
            os.remove("sleepyBaby.db")
            
        conn = sqlite3.connect("sleepyBaby.db")
        cursor = conn.cursor()

        # جدول الأطفال
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS children (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL CHECK(length(name) > 0),
            birthDate INTEGER NOT NULL,
            gender TEXT NOT NULL CHECK(gender IN ('M', 'F')),
            sleepHour INTEGER NOT NULL CHECK(sleepHour BETWEEN 0 AND 23),
            sleepMinute INTEGER NOT NULL CHECK(sleepMinute BETWEEN 0 AND 59),
            wakeHour INTEGER NOT NULL CHECK(wakeHour BETWEEN 0 AND 23),
            wakeMinute INTEGER NOT NULL CHECK(wakeMinute BETWEEN 0 AND 59),
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            updated_at INTEGER DEFAULT (strftime('%s', 'now'))
        )
        """)

        # إنشاء فهرس للبحث عن الأطفال
        cursor.execute("""
        CREATE INDEX IF NOT EXISTS idx_children_name ON children(name)
        """)

        # جدول المنبهات
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS alarms (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            child_id INTEGER NOT NULL,
            hour INTEGER NOT NULL CHECK(hour BETWEEN 0 AND 23),
            minute INTEGER NOT NULL CHECK(minute BETWEEN 0 AND 59),
            enabled INTEGER NOT NULL DEFAULT 1 CHECK(enabled IN (0, 1)),
            repeat_days TEXT CHECK(
                repeat_days IS NULL OR 
                (
                    repeat_days LIKE '%0%' OR 
                    repeat_days LIKE '%1%' OR 
                    repeat_days LIKE '%2%' OR 
                    repeat_days LIKE '%3%' OR 
                    repeat_days LIKE '%4%' OR 
                    repeat_days LIKE '%5%' OR 
                    repeat_days LIKE '%6%'
                )
            ),
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            updated_at INTEGER DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE
        )
        """)

        # إنشاء فهرس للمنبهات النشطة
        cursor.execute("""
        CREATE INDEX IF NOT EXISTS idx_active_alarms ON alarms(enabled, hour, minute) WHERE enabled = 1
        """)

        # جدول سجلات النوم
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS sleep_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            child_id INTEGER NOT NULL,
            start_time INTEGER NOT NULL,
            end_time INTEGER,
            quality INTEGER CHECK(quality BETWEEN 1 AND 5),
            notes TEXT,
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            updated_at INTEGER DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE,
            CHECK(end_time IS NULL OR end_time > start_time)
        )
        """)

        # إنشاء فهرس لسجلات النوم
        cursor.execute("""
        CREATE INDEX IF NOT EXISTS idx_sleep_records_child ON sleep_records(child_id, start_time)
        """)

        # جدول إحصائيات النوم
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS sleep_statistics (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            child_id INTEGER NOT NULL,
            date INTEGER NOT NULL,
            total_sleep_minutes INTEGER CHECK(total_sleep_minutes >= 0),
            number_of_sleeps INTEGER CHECK(number_of_sleeps >= 0),
            average_sleep_quality REAL CHECK(average_sleep_quality BETWEEN 1 AND 5),
            longest_sleep_minutes INTEGER CHECK(longest_sleep_minutes >= 0),
            shortest_sleep_minutes INTEGER CHECK(shortest_sleep_minutes >= 0),
            created_at INTEGER DEFAULT (strftime('%s', 'now')),
            updated_at INTEGER DEFAULT (strftime('%s', 'now')),
            FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE,
            UNIQUE(child_id, date)
        )
        """)

        # إنشاء فهرس للإحصائيات
        cursor.execute("""
        CREATE INDEX IF NOT EXISTS idx_sleep_stats_child ON sleep_statistics(child_id, date)
        """)

        # Trigger لتحديث updated_at في جدول الأطفال
        cursor.execute("""
        CREATE TRIGGER IF NOT EXISTS update_children_timestamp 
        AFTER UPDATE ON children
        BEGIN
            UPDATE children SET updated_at = strftime('%s', 'now') 
            WHERE id = NEW.id;
        END;
        """)

        # Trigger لتحديث updated_at في جدول المنبهات
        cursor.execute("""
        CREATE TRIGGER IF NOT EXISTS update_alarms_timestamp 
        AFTER UPDATE ON alarms
        BEGIN
            UPDATE alarms SET updated_at = strftime('%s', 'now') 
            WHERE id = NEW.id;
        END;
        """)

        conn.commit()
        print("تم إنشاء قاعدة البيانات sleepyBaby.db بنجاح")
        
    except sqlite3.Error as e:
        print(f"حدث خطأ أثناء إنشاء قاعدة البيانات: {e}")
    
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    create_database()
