package uk.ac.tees.mad.packright.model

data class CategoryModel(
    val categoryId: String = "",
    val categoryName: String = "",
    val items: List<ItemModel> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {

    val totalItems: Int
        get() = items.size

    val packedItems: Int
        get() = items.count { it.isPacked }

    val progressPercentage: Float
        get() = if (totalItems == 0) 0f
        else (packedItems.toFloat() / totalItems) * 100f
}