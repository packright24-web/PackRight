package uk.ac.tees.mad.packright.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryOwnerId")]
)
@Serializable
data class ItemEntity(

    @PrimaryKey
    val itemId: String,

    val categoryOwnerId: String,

    val userId: String,

    val itemName: String,
    val isPacked: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val isSynced: Boolean = false
)