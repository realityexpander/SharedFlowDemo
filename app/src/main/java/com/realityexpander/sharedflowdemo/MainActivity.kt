package com.realityexpander.sharedflowdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.realityexpander.sharedflowdemo.ui.theme.SharedFlowDemoTheme
import kotlinx.coroutines.launch


// https://www.youtube.com/watch?v=QNrNKPKe5oc (should you use SharedFlow)
// https://www.youtube.com/watch?v=6v8iJDJdtMc (use channels to send onetime events)

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


        // Also valid:
//        lifecycleScope.launchWhenStarted {
//            viewModel.eventFlow.collect { event ->
//                when(event) {
//                    is MainViewModel.MyEvent.ErrorEvent -> {
//                        //Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
//                        println("Error: ${event.message}")
//                    }
//                }
//            }
//        }

        setContent {
            SharedFlowDemoTheme {

                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(true) {
                    viewModel.eventFlow.collect { event ->
                        when(event) {
                            is MainViewModel.MyEvent.ErrorEvent -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                            }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Greeting("Android")

                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }

        viewModel.triggerEvent()
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