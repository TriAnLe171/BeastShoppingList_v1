package com.example.shoppinglist.di

import android.content.Context
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.ShoppingListDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideAppDatabase(appDatabase: AppDatabase): ShoppingListDAO {
        return appDatabase.shoppingItemDao()
    }

    @Provides
    @Singleton
    fun provideShoppingListDAO(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }
}