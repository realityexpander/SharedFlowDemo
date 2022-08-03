package com.realityexpander.sharedflowdemo

import android.content.Intent
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.realityexpander.sharedflowdemo.ui.theme.SharedFlowDemoTheme
import kotlinx.coroutines.launch


// https://www.youtube.com/watch?v=QNrNKPKe5oc (should you use SharedFlow)

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Collect the sharedFlow (may have missing events between onStop and onDestroy)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {  // cancel during onStop (not onDestroy), restart on onStart (ie: between configuration changes)
                viewModel.sharedFlow.collect { number ->
                    println("Collected $number from shared flow")
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {  // will collect all values
                viewModel.channelFlow.collect { number ->
                    println("Collected $number from channel")
                }
            }
        }

        Intent(this, MyService::class.java).also {
            startService(it)
        }

        setContent {
            SharedFlowDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Greeting("Android")
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
    SharedFlowDemoTheme {
        Greeting("Android")
    }
}