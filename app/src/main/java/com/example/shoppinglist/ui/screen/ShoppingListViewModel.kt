package com.example.shoppinglist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.data.ShoppingListDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    val dao: ShoppingListDAO
) : ViewModel() {

    val defaultCategories = listOf("Electronics",
        "Dairy",
        "Food",
        "Vegetables",
        "Fruits",
        "Beverages",
        "Books",
        "Clothing",
        "Household",
        "Beauty & Personal Care",
        "Health & Wellness",
        "Toys & Games",
        "Sports & Outdoors",
        "Pet Supplies").sortedBy {
        it.lowercase()
    }

    val defaultPriority = listOf("All", "Normal", "High")

    val shoppingItems: StateFlow<List<ShoppingItem>> = dao.getItemsSortedByPriority()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItemsSortedByPrice: StateFlow<List<ShoppingItem>> = dao.getItemsSortedByPrice()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<String>> = shoppingItems
        .map { items ->
            listOf("All") + (defaultCategories + items.map { it.category }).distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All") + defaultCategories)

    suspend fun getAllItemsNum(): Int {
        return dao.getAllItemsNum()
    }

    suspend fun getImportantItemsNum(): Int {
        return dao.getImportantItemsNum()
    }

    fun addItem(item: ShoppingItem) {
        viewModelScope.launch { dao.insertItem(item) }
    }

    fun updateItem(item: ShoppingItem, newCategory: String) {
        val updatedItem = item.copy(category = newCategory)
        viewModelScope.launch { dao.updateItem(updatedItem) }
    }

    fun changeItemState(item: ShoppingItem, value: Boolean) {
        val updatedItem = item.copy()
        updatedItem.isBought = value
        viewModelScope.launch { dao.updateItem(updatedItem) }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch { dao.deleteItem(item) }
    }

    fun deleteAllItems() {
        viewModelScope.launch { dao.deleteAllItems() }
    }

    fun filterItems(
        category: String,
        priority: String,
        status: String,
        searchQuery: String
    ): StateFlow<List<ShoppingItem>> {
        return shoppingItems.map { items ->
            items.filter {
                (category == "All" || it.category == category) &&
                (priority == "All" || it.priority.name.equals(priority, ignoreCase = true)) &&
                (status == "All" ||
                    (status == "Pending" && !it.isBought) ||
                    (status == "Bought" && it.isBought)) &&
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
