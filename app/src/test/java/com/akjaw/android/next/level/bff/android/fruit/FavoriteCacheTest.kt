package com.akjaw.android.next.level.bff.android.fruit

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class FavoriteCacheTest {

    private lateinit var fakeApi: FakeFruitApi
    private lateinit var systemUnderTest: FavoriteCache

    @Before
    fun setUp() {
        fakeApi = FakeFruitApi()
        systemUnderTest = FavoriteCache(fakeApi)
    }

    @Test
    fun `Initially cache is empty`() {
        systemUnderTest.favoriteFruitIds.value shouldBe emptyList()
    }

    @Test
    fun `On initialization favorites from the API are set`() = runTest {
        val favorites = listOf(1, 2)
        fakeApi.favorites = favorites.toMutableList()

        systemUnderTest.initialize()

        systemUnderTest.favoriteFruitIds.value shouldBe favorites
    }

    @Test
    fun `Adding a favorite should update the current favorites on API success`() = runTest {
        systemUnderTest.updateFavorite(1)

        systemUnderTest.favoriteFruitIds.value shouldBe listOf(1)
    }

    @Test
    fun `Removing a favorite should update the current favorites on API success`() = runTest {
        systemUnderTest.updateFavorite(1)

        systemUnderTest.updateFavorite(1)

        systemUnderTest.favoriteFruitIds.value shouldBe emptyList()
    }

    @Test
    fun `Adding a favorite should not change current favorites on API failure`() = runTest {
        fakeApi.shouldAddFail = true

        systemUnderTest.updateFavorite(1)

        systemUnderTest.favoriteFruitIds.value shouldBe emptyList()
    }

    @Test
    fun `Removing a favorite should not change current favorites on API failure`() = runTest {
        systemUnderTest.updateFavorite(1)
        fakeApi.shouldRemoveFail = true

        systemUnderTest.updateFavorite(1)

        systemUnderTest.favoriteFruitIds.value shouldBe listOf(1)
    }
}