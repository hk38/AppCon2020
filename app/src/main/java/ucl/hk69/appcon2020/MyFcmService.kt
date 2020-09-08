package ucl.hk69.appcon2020

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.realm.Realm


class MyFcmService: FirebaseMessagingService() {
    override fun onMessageReceived(msg: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val channelId = "channel_id"
        val channelName = "channel_name"
        val channelDescription = "channel_description "

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply { description = channelDescription }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(msg.notification?.title ?: "notification")
            .setContentText(msg.notification?.body ?: "message")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(this).notify(0, builder.build())
    }

    override fun onNewToken(p: String) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val setting = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()
            setting?.token = p
            setting?.tokenCanged = true
            Log.d("token", p)
        }
    }


}