package com.realityexpander.sharedflowdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    // Hot flow of data, can have multiple subscribers, in multiple places (not just same viewmodel)
    private val _sharedFlow = MutableSharedFlow<Int>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    // Cold flow of data, can only have 1 subscriber, and will pause when no subscribers, and re-emit all the events when a subscriber is added
    private val channel = Channel<Int>()
    val channelFlow = channel.receiveAsFlow()



    sealed class MyEvent {
        data class ErrorEvent(val message: String): MyEvent()
    }

    private val eventChannel = Channel<MyEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun triggerEvent() = viewModelScope.launch {
        eventChannel.send(MyEvent.ErrorEvent("This is an error"))
    }

    init {

        // Send the event in the sharedFlow, useful for session events with multiple observers
        viewModelScope.launch {
            repeat(10000) {
                _sharedFlow.emit(it)  // still sent if no observers (events are lost if no observers)
                delay(1000L)
            }
        }

        // Send the event in the channel, for things like snackbar events with 1 observer
        viewModelScope.launch(Dispatchers.Main.immediate) {  // this is the default dispatcher for ViewModelScope
            repeat(10000) {
                channel.send(it)
                delay(1000L)
            }
        }
    }


}