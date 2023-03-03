package com.akjaw.android.next.level.ktor.fruit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteCache(
    private val api: FruitApi
) {

    private val mutableFavoriteFruitIds: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList<Int>())
    val favoriteFruitIds: StateFlow<List<Int>> = mutableFavoriteFruitIds

    suspend fun initialize() {
        val favorites = api.getFavorites()
        mutableFavoriteFruitIds.value = favorites
    }

    suspend fun updateFavorite(fruitId: Int) {
        val favoriteExists = favoriteFruitIds.value.contains(fruitId)
        if (favoriteExists) {
            val wasRemoved = api.removeFavorite(fruitId)
            if (wasRemoved) {
                mutableFavoriteFruitIds.value = favoriteFruitIds.value.filter { it != fruitId }
            }
        } else {
            val wasAdded = api.addFavorite(fruitId)
            if (wasAdded) {
                mutableFavoriteFruitIds.value = favoriteFruitIds.value + fruitId
            }
        }
    }
}
