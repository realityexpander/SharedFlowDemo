package com.realityexpander.sharedflowdemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlin.concurrent.thread

class MyService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        thread {
            while (true) {
                println("Service is running...")
                Thread.sleep(1000)
            }
        }
    }
}