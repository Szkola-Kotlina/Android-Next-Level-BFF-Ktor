package com.akjaw.android.next.level.ktor.fruit

import com.akjaw.android.next.level.ktor.fruit.model.Fruit
import com.akjaw.android.next.level.ktor.fruit.model.Nutritions
import com.akjaw.android.next.level.ktor.shared.model.FruitSchema
import com.akjaw.android.next.level.ktor.shared.model.NutritionsSchema
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class FruitListViewModelTest {

    companion object {
        private val FRUITS_SCHEMA =
            listOf(FruitSchema(name = "Apple"), FruitSchema(name = "Banana"), FruitSchema(name = "Cherry"))
        private val FRUITS = listOf(Fruit(name = "Apple"), Fruit(name = "Banana"), Fruit(name = "Cherry"))
        private val FRUITS_WITH_FILTER = listOf(Fruit(name = "Apple"), Fruit(name = "Banana"))
    }

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeFruitApi: FakeFruitApi
    private lateinit var systemUnderTest: FruitListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeFruitApi = FakeFruitApi()
        systemUnderTest = FruitListViewModel(fakeFruitApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @Ignore
    fun apiCall() = runTest(testDispatcher) {
        val fruits = KtorFruitApi().getFruits()
        fruits shouldHaveSize 39
    }

    @Test
    fun `The fruit structure is correctly converted`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(
                name = "Apple",
                id = 2,
                nutritions = NutritionsSchema(
                    carbohydrates = 1f,
                    protein = 2f,
                    fat = 3f,
                    calories = 4f,
                    sugar = 5f,
                )
            )
        )

        systemUnderTest.initialize()

        systemUnderTest.fruits.value.first() shouldBe Fruit(
            name = "Apple",
            id = 2,
            nutritions = Nutritions(
                carbohydrates = 1f,
                protein = 2f,
                fat = 3f,
                calories = 4f,
                sugar = 5f,
            )
        )
    }

    @Test
    fun `Initialize updates the state with fruits fetched from the API sorted by name`() {
        fakeFruitApi.fruits = FRUITS_SCHEMA.reversed()

        systemUnderTest.initialize()

        systemUnderTest.fruits.value shouldBe FRUITS
    }

    @Test
    fun `Filtering by name updates the state to include only matching case insensitive fruit names`() {
        fakeFruitApi.fruits = FRUITS_SCHEMA
        systemUnderTest.initialize()

        systemUnderTest.filterByName("a")

        systemUnderTest.fruits.value shouldBe FRUITS_WITH_FILTER
    }

    @Test
    fun `Removing the name filter updates the state with the original list`() {
        fakeFruitApi.fruits = FRUITS_SCHEMA
        systemUnderTest.initialize()
        systemUnderTest.filterByName("a")

        systemUnderTest.filterByName("")

        systemUnderTest.fruits.value shouldBe FRUITS
    }

    @Test
    fun `Initializing again correctly applies existing name filter`() {
        fakeFruitApi.fruits = FRUITS_SCHEMA
        systemUnderTest.initialize()
        systemUnderTest.filterByName("a")

        systemUnderTest.initialize()

        systemUnderTest.fruits.value shouldBe FRUITS_WITH_FILTER
    }

    @Test
    fun `Sorting by CARBOHYDRATES updates the state correctly`() = nutritionSortingTestCase(
        nutrition = FruitListViewModel.SortType.CARBOHYDRATES,
        fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(carbohydrates = 10f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(carbohydrates = 22f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(carbohydrates = 11f)),
        ),
        assertion = {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Cherry"
            get(2).name shouldBe "Banana"
        }
    )

    @Test
    fun `Sorting by PROTEIN updates the state correctly`() = nutritionSortingTestCase(
        nutrition = FruitListViewModel.SortType.PROTEIN,
        fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(protein = 10f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(protein = 22f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(protein = 11f)),
        ),
        assertion = {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Cherry"
            get(2).name shouldBe "Banana"
        }
    )

    @Test
    fun `Sorting by FAT updates the state correctly`() = nutritionSortingTestCase(
        nutrition = FruitListViewModel.SortType.FAT,
        fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(fat = 10f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(fat = 22f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(fat = 11f)),
        ),
        assertion = {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Cherry"
            get(2).name shouldBe "Banana"
        }
    )

    @Test
    fun `Sorting by CALORIES updates the state correctly`() = nutritionSortingTestCase(
        nutrition = FruitListViewModel.SortType.CALORIES,
        fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(calories = 10f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(calories = 22f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(calories = 11f)),
        ),
        assertion = {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Cherry"
            get(2).name shouldBe "Banana"
        }
    )

    @Test
    fun `Sorting by SUGAR updates the state correctly`() = nutritionSortingTestCase(
        nutrition = FruitListViewModel.SortType.SUGAR,
        fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(sugar = 10f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(sugar = 22f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(sugar = 11f)),
        ),
        assertion = {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Cherry"
            get(2).name shouldBe "Banana"
        }
    )

    @Test
    fun `Filtering after sorting keeps the correct sorting`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(carbohydrates = 3f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(carbohydrates = 1f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(carbohydrates = 2f)),
        )
        systemUnderTest.initialize()
        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.CARBOHYDRATES)

        systemUnderTest.filterByName("a")

        assertSoftly(systemUnderTest.fruits.value) {
            shouldHaveSize(2)
            get(0).name shouldBe "Banana"
            get(1).name shouldBe "Apple"
        }
    }

    @Test
    fun `Sorting after filtering keeps the correct filter`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(carbohydrates = 3f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(carbohydrates = 1f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(carbohydrates = 2f)),
        )
        systemUnderTest.initialize()
        systemUnderTest.filterByName("a")

        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.CARBOHYDRATES)

        assertSoftly(systemUnderTest.fruits.value) {
            shouldHaveSize(2)
            get(0).name shouldBe "Banana"
            get(1).name shouldBe "Apple"
        }
    }

    @Test
    fun `Removing sorting brings back original sorting`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(name = "Apple", nutritions = NutritionsSchema(carbohydrates = 3f)),
            FruitSchema(name = "Banana", nutritions = NutritionsSchema(carbohydrates = 1f)),
            FruitSchema(name = "Cherry", nutritions = NutritionsSchema(carbohydrates = 2f)),
        )
        systemUnderTest.initialize()
        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.CARBOHYDRATES)

        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.NO_SORTING)
        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.NO_SORTING)
        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.NO_SORTING)

        assertSoftly(systemUnderTest.fruits.value) {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Banana"
            get(2).name shouldBe "Cherry"
        }
    }

    @Test
    fun `Initially the favorite status is false`() {
        fakeFruitApi.fruits = listOf(FruitSchema(name = "Apple", id = 1))
        systemUnderTest.initialize()

        systemUnderTest.fruits.value.first().isFavorited shouldBe false
    }

    @Test
    fun `Updating a favorite fruit the favorite flag`() {
        fakeFruitApi.fruits = listOf(FruitSchema(name = "Apple", id = 1))
        systemUnderTest.initialize()

        systemUnderTest.updateFavorite(1)

        systemUnderTest.fruits.value.first().isFavorited shouldBe true
    }

    @Test
    fun `Updating an existing favorite fruit removes the favorite list`() {
        fakeFruitApi.fruits = listOf(FruitSchema(name = "Apple", id = 1))
        systemUnderTest.initialize()
        systemUnderTest.updateFavorite(1)

        systemUnderTest.updateFavorite(1)

        systemUnderTest.fruits.value.first().isFavorited shouldBe false
    }

    @Test
    fun `Favorite fruits are placed at the top when there is no sorting`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(name = "Apple", id = 3),
            FruitSchema(name = "Banana", id = 2),
            FruitSchema(name = "Cherry", id = 1),
        )
        systemUnderTest.initialize()

        systemUnderTest.updateFavorite(1)

        assertSoftly(systemUnderTest.fruits.value) {
            shouldHaveSize(3)
            get(0).name shouldBe "Cherry"
            get(1).name shouldBe "Apple"
            get(2).name shouldBe "Banana"
        }
    }

    @Test
    fun `Favorite fruits dont change the activated sorting`() {
        fakeFruitApi.fruits = listOf(
            FruitSchema(name = "Apple", id = 3),
            FruitSchema(name = "Banana", id = 2),
            FruitSchema(name = "Cherry", id = 1),
        )
        systemUnderTest.initialize()
        systemUnderTest.sortByNutrition(FruitListViewModel.SortType.CARBOHYDRATES)

        systemUnderTest.updateFavorite(1)

        assertSoftly(systemUnderTest.fruits.value) {
            shouldHaveSize(3)
            get(0).name shouldBe "Apple"
            get(1).name shouldBe "Banana"
            get(2).name shouldBe "Cherry"
        }
    }

    private fun nutritionSortingTestCase(
        nutrition: FruitListViewModel.SortType,
        fruits: List<FruitSchema>,
        assertion: List<Fruit>.() -> Unit
    ) {
        fakeFruitApi.fruits = fruits
        systemUnderTest.initialize()

        systemUnderTest.sortByNutrition(nutrition)

        assertSoftly(systemUnderTest.fruits.value) {
            assertion(systemUnderTest.fruits.value)
        }
    }
}

class FakeFruitApi : FruitApi {

    var fruits: List<FruitSchema> = emptyList()

    override suspend fun getFruits(): List<FruitSchema> = fruits
}