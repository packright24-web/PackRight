package uk.ac.tees.mad.packright.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, ItemEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun itemDao(): ItemDao
}