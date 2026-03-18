package uk.ac.tees.mad.packright.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "categories")
data class CategoryEntity(

    @PrimaryKey
    val categoryId: String,

    val userId: String,  // Foreign key to the user table


    val categoryName: String,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    val isSynced: Boolean = false
)