package com.example.kotlinflow


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class UserViewModel : ViewModel() {

    // 🔹 1. Using Flow to Fetch Dummy User List
    fun fetchUsers(): Flow<List<User>> = flow {
        delay(2000) // Simulate network delay
        val users = listOf(
            User(1, "John Doe"),
            User(2, "Emma Watson"),
            User(3, "Michael Smith"),
            User(4, "Sophia Johnson"),
            User(5, "David Wilson")
        )
        emit(users) // Emit list of users
    }

    // 🔹 2. Using StateFlow to Manage Count State
    private val _count = MutableStateFlow(0)  // Default count is 0
    val count: StateFlow<Int> = _count.asStateFlow() // Expose as read-only

    // Function to increase count
    fun incrementCount() {
        _count.value += 1
    }

    // 🔹 3. Using SharedFlow for One-time Events
    private val _eventFlow = MutableSharedFlow<String>() // SharedFlow for messages
    val eventFlow = _eventFlow.asSharedFlow() // Expose as read-only

    fun triggerEvent(message: String) {
        viewModelScope.launch {
            _eventFlow.emit(message) // Emit event
        }
    }



    // 🔹 Using ChannelFlow for Real-time Updates
    private val _userChannel = MutableSharedFlow<List<User>>(replay = 1)
    val userChannelFlow = _userChannel.asSharedFlow()

    init {
        fetchUserUpdates()
    }

    private fun fetchUserUpdates() {
        viewModelScope.launch {
            channelFlow {
                while (true) {
                    delay(3000) // Simulate new users joining every 3 seconds
                    val users = listOf(
                        User(1, "John Doe"),
                        User(2, "Emma Watson"),
                        User(3, "Michael Smith"),
                        User(4, "Sophia Johnson"),
                        User(5, "David Wilson"),
                        User(6, "Chris Evans") // New User
                    )
                    send(users) // Send new user list
                }
            }.collect { updatedUsers ->
                _userChannel.emit(updatedUsers)
            }
        }
    }
}