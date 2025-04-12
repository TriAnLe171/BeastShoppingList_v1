package com.example.shoppinglist.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.shoppinglist.ui.navigation.SummaryScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var allItem by mutableStateOf(0)
    var importantItem by mutableStateOf(0)
    var boughtItem by mutableStateOf(0)

    init {
        allItem = savedStateHandle.toRoute<SummaryScreenRoute>().allItems
        importantItem = savedStateHandle.toRoute<SummaryScreenRoute>().importantItems
        boughtItem = savedStateHandle.toRoute<SummaryScreenRoute>().boughtItems
    }
}
