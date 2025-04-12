package com.example.shoppinglist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDAO {
    @Query("SELECT * FROM shopping_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Query("SELECT COUNT(*) FROM shopping_items")
    suspend fun getAllItemsNum(): Int

    @Query("SELECT COUNT(*) FROM shopping_items WHERE priority = 'HIGH'")
    suspend fun getImportantItemsNum(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM shopping_items WHERE isBought = 1")
    suspend fun deleteBoughtItems()

    @Query("SELECT * FROM shopping_items ORDER BY estimated_price ASC")
    fun getItemsSortedByPrice(): Flow<List<ShoppingItem>>

    @Query(
        """
    SELECT * FROM shopping_items 
    ORDER BY 
        CASE WHEN isBought = 0 AND priority = 'HIGH' THEN 1
             WHEN isBought = 0 AND priority = 'NORMAL' THEN 2
             WHEN isBought = 1 AND priority = 'HIGH' THEN 3
             WHEN isBought = 1 AND priority = 'NORMAL' THEN 4
        END
"""
    )
    fun getItemsSortedByPriority(): Flow<List<ShoppingItem>>
}
