package com.akjaw.android.next.level.bff.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.akjaw.android.next.level.bff.android.fruit.FruitListScreen
import com.akjaw.android.next.level.bff.android.fruit.FruitListViewModel
import com.akjaw.android.next.level.bff.android.fruit.FruitListViewModelFactory
import com.akjaw.android.next.level.bff.android.ui.theme.FruitTheme

class MainActivity : ComponentActivity() {

    private val fruitListViewModel: FruitListViewModel by viewModels(factoryProducer = { FruitListViewModelFactory() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FruitTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    FruitListScreen(fruitListViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FruitTheme {
        Greeting("Android")
    }
}