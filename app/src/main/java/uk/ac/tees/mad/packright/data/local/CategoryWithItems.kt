package uk.ac.tees.mad.packright.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithItems(

    @Embedded
    val category: CategoryEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryOwnerId"
    )
    val items: List<ItemEntity>
)