package com.kth.kotlinblemodule

import android.content.Context
import androidx.room.*

@Database(entities = arrayOf(BleModel::class), version = 1, exportSchema = false)
@TypeConverters(DeviceTypeConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun bleDao() :BleDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        internal fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {

                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "ble_database"
                    )
                        .build()
                }

            }
            return INSTANCE
        }
    }
}