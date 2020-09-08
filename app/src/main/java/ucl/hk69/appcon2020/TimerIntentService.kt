package ucl.hk69.appcon2020

import android.app.IntentService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import io.realm.Realm
import java.io.DataOutputStream
import java.lang.Exception
import java.lang.Thread.sleep
import java.util.*

class TimerIntentService: IntentService("TimerIntentService") {
    companion object {
        const val TAG = "TimerIntentService"
    }

    override fun onHandleIntent(intent: Intent?) {
        val limit = intent?.getIntExtra("lim", -1) ?: -1
        Log.d("receive", "bgColor:${intent?.getStringExtra("bgColor")}, textColor:${intent?.getStringExtra("textColor")}, title:${intent?.getStringExtra("title")}, subTitle:${intent?.getStringExtra("subTitle")}")

        val sender = SendData(
            intent?.getStringExtra("title")?:"title is null",
            intent?.getStringExtra("subTitle")?:"subtitle is null",
            intent?.getStringExtra("bgColor")?:"FFFFFFFF",
            intent?.getStringExtra("textColor")?:"FF000000",
            applicationContext)
        if(limit < 0) return
        for (i in 1..limit){
            sleep(60000)
            sender.send()
        }
    }
}