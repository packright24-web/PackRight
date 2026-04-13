package uk.ac.tees.mad.packright.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)


    @Query("SELECT * FROM items WHERE categoryOwnerId = :categoryId")
    fun getItemsByCategory(categoryId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE isSynced = 0")
    suspend fun getUnsyncedItems(): List<ItemEntity>
}