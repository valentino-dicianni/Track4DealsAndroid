package com.example.track4deals.services.utils

import java.io.IOException

class NoInternetException : IOException() {
    override val message: String
        get() = "No internet available, please check your connected WIFi or Data"
}