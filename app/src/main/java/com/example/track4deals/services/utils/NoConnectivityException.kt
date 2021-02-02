package com.example.track4deals.services.utils

import java.io.IOException

class NoConnectivityException : IOException(){
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
}