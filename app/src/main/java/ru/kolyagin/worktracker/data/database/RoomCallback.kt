package ru.kolyagin.worktracker.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject

class RoomCallback @Inject constructor(): RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
            """
                INSERT INTO DaySchedule VALUES (0, 0), (1, 0), (2, 0), (3, 0), (4, 0), (5, 0), (6, 0)
            """.trimIndent()
        )
    }
}