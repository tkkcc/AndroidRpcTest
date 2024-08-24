package com.example.androidrpctest

import androidx.activity.ComponentActivity
import kotlin.concurrent.thread

class Native {
    external fun start()
}


fun startNativeRpcServer(context:ComponentActivity) {

    thread {
        System.loadLibrary("rust")
        Native().start()
    }
}