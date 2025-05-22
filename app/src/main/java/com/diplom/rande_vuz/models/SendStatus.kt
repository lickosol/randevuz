package com.diplom.rande_vuz.models


sealed class SendStatus {
        object Success : SendStatus()
        data class Error(val exception: Throwable) : SendStatus()

}