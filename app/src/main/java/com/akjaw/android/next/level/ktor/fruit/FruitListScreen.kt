package com.akjaw.android.next.level.ktor.fruit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akjaw.android.next.level.ktor.fruit.FruitListViewModel.SortType
import com.akjaw.android.next.level.ktor.fruit.model.Fruit

@Composable
fun FruitListScreen(viewModel: FruitListViewModel) {
    val fruits by viewModel.fruits.collectAsState()

    LaunchedEffect(null) {
        viewModel.initialize()
    }

    FruitListScreenContent(
        fruits = fruits,
        filterByName = viewModel::filterByName,
        sortByNutrition = viewModel::sortByNutrition,
        updateFavorite = viewModel::updateFavorite,
    )
}

@Composable
private fun FruitListScreenContent(
    fruits: List<Fruit>,
    filterByName: (String) -> Unit,
    sortByNutrition: (SortType) -> Unit,
    updateFavorite: (Int) -> Unit
) {
    var currentSortType by remember { mutableStateOf(SortType.NO_SORTING) }
    val shouldScrollToTop = remember(currentSortType) { currentSortType == SortType.NO_SORTING }
    Column(Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        TopActions(
            filterByName = filterByName,
            sortByNutrition = { newSortType ->
                currentSortType = newSortType
                sortByNutrition(newSortType)
            },
        )
        FruitList(
            fruits = fruits,
            updateFavorite = updateFavorite,
            shouldScrollToTop = shouldScrollToTop
        )
    }
}

@Composable
private fun TopActions(
    filterByName: (String) -> Unit,
    sortByNutrition: (SortType) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    var isDropDownShown by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    filterByName(newValue)
                },
                label = { Text("Search for fruit") }
            )
            IconButton(
                onClick = { isDropDownShown = true },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = "Sort",
                    modifier = Modifier
                )
            }
        }
        Box(contentAlignment = Alignment.TopEnd) {
            DropdownMenu(
                expanded = isDropDownShown,
                onDismissRequest = { isDropDownShown = false },
            ) {
                val onClick: (SortType) -> Unit = remember {
                    { sortType ->
                        isDropDownShown = false
                        sortByNutrition(sortType)
                    }
                }
                Column {
                    DropdownMenuItem(onClick = { onClick(SortType.NO_SORTING) }) {
                        Text("No sorting")
                    }
                    DropdownMenuItem(onClick = { onClick(SortType.CARBOHYDRATES) }) {
                        Text("Carbohydrates")
                    }
                    DropdownMenuItem(onClick = { onClick(SortType.PROTEIN) }) {
                        Text("Protein")
                    }
                    DropdownMenuItem(onClick = { onClick(SortType.FAT) }) {
                        Text("Fat")
                    }
                    DropdownMenuItem(onClick = { onClick(SortType.CALORIES) }) {
                        Text("Calories")
                    }
                    DropdownMenuItem(onClick = { onClick(SortType.SUGAR) }) {
                        Text("Sugar")
                    }
                }
            }
        }
    }
}

@Composable
private fun FruitList(
    fruits: List<Fruit>,
    updateFavorite: (Int) -> Unit,
    shouldScrollToTop: Boolean,
) {
    val state = rememberLazyListState()
    LaunchedEffect(fruits) {
        if (shouldScrollToTop) {
            state.scrollToItem(0)
        }
    }
    LazyColumn(Modifier.fillMaxHeight(), state = state) {
        items(items = fruits, key = { it.id }) { fruit ->
            FruitItem(
                fruit = fruit,
                isFavorited = fruit.isFavorited,
                onFavoriteClick = { updateFavorite(fruit.id) }
            )
        }
    }
}

@Composable
private fun FruitItem(fruit: Fruit, isFavorited: Boolean, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        backgroundColor = Color(0xFFe0f0f6),
        elevation = 4.dp,
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(fruit.name, style = MaterialTheme.typography.h5)
                IconButton(onClick = onFavoriteClick) {
                    if (isFavorited) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favorited"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.StarBorder,
                            contentDescription = "Add to favorite",
                        )
                    }
                }
            }
            NutritionRow {
                Text("Calories: ${fruit.nutritions.calories}")
            }
            NutritionRow {
                Text("Carbohydrates: ${fruit.nutritions.carbohydrates}")
                Text("Sugar: ${fruit.nutritions.sugar}")
            }
            NutritionRow {
                Text("Protein: ${fruit.nutritions.protein}")
                Text("Fat: ${fruit.nutritions.fat}")
            }
        }
    }
}

@Composable
private fun NutritionRow(content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        content()
    }
}
