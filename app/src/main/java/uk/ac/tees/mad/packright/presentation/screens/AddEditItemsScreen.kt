package uk.ac.tees.mad.packright.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.packright.data.local.ItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemsScreen(
    categoryId: String,
    items: List<ItemEntity>,
    onBack: () -> Unit,
    onAddItem: (String) -> Unit,
    onToggleItem: (ItemEntity) -> Unit,
    onDeleteItem: (ItemEntity) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Essentials", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3b82f6))
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Add New Item Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    placeholder = { Text("New item...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            onAddItem(newItemName)
                            newItemName = ""
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Item",
                        tint = Color(0xFF3b82f6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.itemId }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isPacked,
                                onCheckedChange = { onToggleItem(item) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.itemName,
                                modifier = Modifier.weight(1f),
                                textDecoration = if (item.isPacked) TextDecoration.LineThrough else null,
                                color = if (item.isPacked) Color.Gray else Color.Black
                            )
                            IconButton(onClick = { itemToDelete = item }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete \"${item.itemName}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteItem(item)
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditItemsScreenPreview() {
    val mockItems = listOf(
        ItemEntity("1", "cat1", "user1", "Passport", true),
        ItemEntity("2", "cat1", "user1", "Charger", false),
        ItemEntity("3", "cat1", "user1", "Camera", false)
    )
    MaterialTheme {
        AddEditItemsScreen(
            categoryId = "cat1",
            items = mockItems,
            onBack = {},
            onAddItem = {},
            onToggleItem = {},
            onDeleteItem = {}
        )
    }
}
