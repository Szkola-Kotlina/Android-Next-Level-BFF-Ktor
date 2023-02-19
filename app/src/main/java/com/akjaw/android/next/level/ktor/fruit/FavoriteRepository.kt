package com.akjaw.android.next.level.ktor.fruit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteRepository {

    private val mutableFavoriteFruitIds: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList<Int>())
    val favoriteFruitIds: StateFlow<List<Int>> = mutableFavoriteFruitIds

    fun updateFavorite(fruitId: Int) {
        mutableFavoriteFruitIds.value = if (favoriteFruitIds.value.contains(fruitId)) {
            favoriteFruitIds.value.filter { it != fruitId }
        } else {
            favoriteFruitIds.value + fruitId
        }
    }
}
