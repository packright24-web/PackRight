package uk.ac.tees.mad.packright.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.packright.data.local.CategoryEntity
import uk.ac.tees.mad.packright.data.local.CategoryWithItems
import uk.ac.tees.mad.packright.model.CategoryModel
import uk.ac.tees.mad.packright.model.ItemModel
import uk.ac.tees.mad.packright.presentation.ViewModel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    categories: List<CategoryWithItems>,
    onCategoryClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PackRight", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { categoryWithItems ->
                val totalItems = categoryWithItems.items.size
                val packedItems = categoryWithItems.items.count { it.isPacked }
                val progress = if (totalItems == 0) 0f else packedItems.toFloat() / totalItems
                
                CategoryCard(
                    categoryName = categoryWithItems.category.categoryName,
                    packedCount = packedItems,
                    totalCount = totalItems,
                    progress = progress,
                    onClick = { onCategoryClick(categoryWithItems.category.categoryId) },
                    onDeleteClick = { categoryToDelete = categoryWithItems.category }
                )
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("New Travel Essential") },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                onAddCategory(newCategoryName)
                                newCategoryName = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        categoryToDelete?.let { category ->
            AlertDialog(
                onDismissRequest = { categoryToDelete = null },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete \"${category.categoryName}\"? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteCategory(category)
                            categoryToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { categoryToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    categoryName: String,
    packedCount: Int,
    totalCount: Int,
    progress: Float,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Category",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$packedCount/$totalCount packed",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockCategories = listOf(
        CategoryWithItems(CategoryEntity("1", "user1", "Clothes", isSynced = true), listOf()),
        CategoryWithItems(
            category = CategoryEntity("2", "user1", "Electronics", isSynced = true),
            items = listOf(
                uk.ac.tees.mad.packright.data.local.ItemEntity("i1", "2", "u1", "Phone", true),
                uk.ac.tees.mad.packright.data.local.ItemEntity("i2", "2", "u1", "Charger", false)
            )
        ),
        CategoryWithItems(CategoryEntity("3", "user1", "Documents", isSynced = true), listOf())
    )
    MaterialTheme {
        HomeScreen(
            categories = mockCategories,
            onCategoryClick = {},
            onProfileClick = {},
            onAddCategory = {},
            onDeleteCategory = {}
        )
    }
}
