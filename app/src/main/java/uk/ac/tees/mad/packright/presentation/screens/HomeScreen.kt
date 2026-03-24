package uk.ac.tees.mad.packright.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
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
    onAddCategory: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PackRight", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3b82f6)) // Blue color from mockup
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color(0xFF3b82f6)) {
                Icon(Icons.Default.Add, contentDescription = "Add Category", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF3F4F6) // Light gray background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    onClick = { onCategoryClick(categoryWithItems.category.categoryId) }
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
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName)
                            newCategoryName = ""
                            showAddDialog = false
                        }
                    }) {
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
    }
}

@Composable
fun CategoryCard(
    categoryName: String,
    packedCount: Int,
    totalCount: Int,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = categoryName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$packedCount/$totalCount packed",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF3b82f6),
                trackColor = Color(0xFFE5E7EB)
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
            onAddCategory = {}
        )
    }
}
