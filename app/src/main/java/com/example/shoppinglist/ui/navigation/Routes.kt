package com.example.shoppinglist.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object ShoppingListScreenRoute

@Serializable
data class SummaryScreenRoute(
    val allItems: Int,
    val importantItems: Int,
    val boughtItems: Int
)