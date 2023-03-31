package ru.kolyagin.worktracker.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject

class RoomCallback @Inject constructor(): RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
            """
                INSERT INTO DaySchedule VALUES (0, 1), (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1)
            """.trimIndent()
        )
    }
}