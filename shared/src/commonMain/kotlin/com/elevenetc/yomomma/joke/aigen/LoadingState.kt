package com.elevenetc.yomomma.joke.aigen

sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    open class Success<T>(val data: T) : LoadingState()
    data class Error(val error: String) : LoadingState()
}

class Joke(val id: String, val joke: String) : LoadingState.Success<String>(joke)

