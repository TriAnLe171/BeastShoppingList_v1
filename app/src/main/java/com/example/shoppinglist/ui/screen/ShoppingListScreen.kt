package com.example.shoppinglist.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppinglist.R
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.data.ShoppingItemPriority
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    onInfoClicked: (Int, Int, Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    var itemToEdit: ShoppingItem? by rememberSaveable { mutableStateOf(null) }
    var showNewItemDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPriority by remember { mutableStateOf("All") }
    var selectedPriceFilter by remember { mutableStateOf("None") }
    var selectedStatus by remember { mutableStateOf("All") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var sortByOption by remember { mutableStateOf("Category") }
    var isSortDropdownExpanded by remember { mutableStateOf(false) }

    val filteredItems by viewModel.filterItems(
        category = selectedCategory,
        priority = selectedPriority,
        status = selectedStatus,
        searchQuery = searchQuery.text
    ).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.beast_shopping_list)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteAllItems()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete all")
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val allItems = viewModel.getAllItemsNum()
                                val importantItems = viewModel.getImportantItemsNum()
                                val boughtItems = shoppingItems.count { it.isBought }
                                onInfoClicked(allItems, importantItems, boughtItems)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Summary")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (showNewItemDialog) {
                ItemDialog(
                    viewModel = viewModel,
                    itemToEdit = itemToEdit,
                    onDismiss = { showNewItemDialog = false }
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small),
                placeholder = {
                    Text(
                        text = "Search items...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Start
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.filter_by), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Button(onClick = {
                        isSortDropdownExpanded = true
                    }) {
                        Text(sortByOption)
                    }
                    DropdownMenu(
                        expanded = isSortDropdownExpanded,
                        onDismissRequest = { isSortDropdownExpanded = false }
                    ) {
                        listOf(stringResource(R.string.category),
                            stringResource(R.string.priority),
                            stringResource(R.string.price),
                            stringResource(R.string.status)
                        ).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sortByOption = option
                                    isSortDropdownExpanded = false
                                    if (sortByOption == "Category") {
                                        selectedPriority = "All"
                                        selectedPriceFilter = "None"
                                        selectedStatus = "All"
                                    } else if (sortByOption == "Priority") {
                                        selectedCategory = "All"
                                        selectedPriceFilter = "None"
                                        selectedStatus = "All"
                                    } else if (sortByOption == "Price") {
                                        selectedCategory = "All"
                                        selectedPriority = "All"
                                        selectedStatus = "All"
                                    } else if (sortByOption == "Status") {
                                        selectedCategory = "All"
                                        selectedPriority = "All"
                                        selectedPriceFilter = "None"
                                    }
                                }
                            )
                        }
                    }
                }
                if (sortByOption == "Status") {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Value: ", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(selectedStatus)
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            listOf(stringResource(R.string.all),
                                stringResource(R.string.pending),
                                stringResource(R.string.bought)
                            ).forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        selectedStatus = status
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                else if (sortByOption == "Category") {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(R.string.value), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(selectedCategory)
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else if (sortByOption == "Priority") {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Value:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(selectedPriority)
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            viewModel.defaultPriority.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority) },
                                    onClick = {
                                        selectedPriority = priority
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else if (sortByOption == "Price") {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Value: ", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(selectedPriceFilter)
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            listOf(stringResource(R.string.low_to_high),
                                stringResource(R.string.high_to_low)).forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter) },
                                    onClick = {
                                        selectedPriceFilter = filter
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_items_yet_add_one_using_the_button))
                }
            } else {
                LazyColumn {
                    items(
                        filteredItems.let {
                            when (selectedPriceFilter) {
                                "Low to High" -> it.sortedBy { item -> item.estimatedPrice }
                                "High to Low" -> it.sortedByDescending { item -> item.estimatedPrice }
                                else -> it
                            }
                        }
                    ) { item ->
                        ShoppingListItemRow(
                            item = item,
                            onCheckedChange = {
                                viewModel.changeItemState(item, it)
                            },
                            onDelete = {
                                viewModel.deleteItem(item)
                            },
                            onEdit = {
                                itemToEdit = item
                                showNewItemDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ItemDialog(
    viewModel: ShoppingListViewModel,
    itemToEdit: ShoppingItem? = null,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var description by remember { mutableStateOf(itemToEdit?.description ?: "") }
    var category by remember { mutableStateOf(itemToEdit?.category ?: "Select Category") }
    var estimatedPrice by remember { mutableStateOf(itemToEdit?.estimatedPrice?.toString() ?: "") }
    var priority by remember { mutableStateOf(itemToEdit?.priority ?: ShoppingItemPriority.NORMAL) }
    var isPriorityDropdownExpanded by remember { mutableStateOf(false) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (itemToEdit == null) stringResource(R.string.add_new_item) else stringResource(
                        R.string.edit_item
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text(stringResource(R.string.name)) },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = stringResource(R.string.name_cannot_be_empty),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descError = false
                    },
                    label = { Text(stringResource(R.string.description)) },
                    isError = descError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (descError) {
                    Text(
                        text = stringResource(R.string.description_cannot_be_empty),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = estimatedPrice,
                    onValueChange = {
                        estimatedPrice = it
                        priceError = false
                    },
                    label = { Text(stringResource(R.string.estimated_price)) },
                    isError = priceError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (priceError) {
                    Text(
                        text = stringResource(R.string.price_must_be_a_valid_number),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Category: ", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isCategoryDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small){
                            Text(category)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                        DropdownMenu(
                            expanded = isCategoryDropdownExpanded,
                            onDismissRequest = { isCategoryDropdownExpanded = false },
                        ) {
                            viewModel.defaultCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryError = false
                                        isCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                if (categoryError) {
                    Text(
                        text = stringResource(R.string.please_select_a_category),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Priority: ", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isPriorityDropdownExpanded = true }) {
                            Text(priority.name)
                        }
                        DropdownMenu(
                            expanded = isPriorityDropdownExpanded,
                            onDismissRequest = { isPriorityDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.normal)) },
                                onClick = {
                                    priority = ShoppingItemPriority.NORMAL
                                    isPriorityDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.high)) },
                                onClick = {
                                    priority = ShoppingItemPriority.HIGH
                                    isPriorityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {

                        val isNameValid = name.isNotBlank()
                        val isDescriptionValid = description.isNotBlank()
                        val isCategoryValid = category != "Select Category"
                        val isPriceValid = estimatedPrice.toIntOrNull() != null

                        if (isNameValid && isDescriptionValid && isCategoryValid && isPriceValid) {
                            if (itemToEdit == null) {
                                viewModel.addItem(
                                    ShoppingItem(
                                        name = name,
                                        description = description,
                                        category = category,
                                        estimatedPrice = estimatedPrice.toInt(),
                                        isBought = false,
                                        priority = priority
                                    )
                                )
                            } else {
                                val updatedItem = itemToEdit.copy(
                                    name = name,
                                    description = description,
                                    category = category,
                                    estimatedPrice = estimatedPrice.toInt(),
                                    priority = priority
                                )
                                viewModel.updateItem(updatedItem, category)
                            }
                            onDismiss()
                        } else {
                            nameError = !isNameValid
                            descError = !isDescriptionValid
                            categoryError = !isCategoryValid
                            priceError = !isPriceValid
                        }
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingListItemRow(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (ShoppingItem) -> Unit
) {
    val cardColor = when (item.priority) {
        ShoppingItemPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
        ShoppingItemPriority.NORMAL -> MaterialTheme.colorScheme.surface
    }
    val contentAlpha = if (item.isBought) 0.5f else 1f

    val categoryIcons = mapOf(
        "Electronics" to R.drawable.ic_electronics,
        "Dairy" to R.drawable.ic_dairy,
        "Food" to R.drawable.ic_food,
        "Vegetables" to R.drawable.ic_vegetables,
        "Fruits" to R.drawable.ic_fruits,
        "Beverages" to R.drawable.ic_beverages,
        "Books" to R.drawable.ic_books,
        "Clothing" to R.drawable.ic_clothing,
        "Household" to R.drawable.ic_household,
        "Beauty & Personal Care" to R.drawable.ic_beauty,
        "Health & Wellness" to R.drawable.ic_health,
        "Toys & Games" to R.drawable.ic_toys,
        "Sports & Outdoors" to R.drawable.ic_sports,
        "Pet Supplies" to R.drawable.ic_pet,
    )

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isBought,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            categoryIcons[item.category]?.let { iconRes ->
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "${item.category} Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            }
            IconButton(onClick = { onEdit(item) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Item")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Item")
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand or close"
                )
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "Description: ${item.description}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Estimated Price: $${item.estimatedPrice}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Category: ${item.category}")
            }
        }
    }
}

