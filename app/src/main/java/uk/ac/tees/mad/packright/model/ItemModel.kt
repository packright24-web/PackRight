package uk.ac.tees.mad.packright.model

data class ItemModel(
    val itemId: String = "",
    val name: String = "",
    val isPacked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)