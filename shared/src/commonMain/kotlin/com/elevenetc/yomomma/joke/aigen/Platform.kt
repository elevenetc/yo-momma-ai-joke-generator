package com.elevenetc.yomomma.joke.aigen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform