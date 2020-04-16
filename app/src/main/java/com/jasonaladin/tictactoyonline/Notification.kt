package com.jasonaladin.tictactoyonline

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL

class Notification (){
    val NOTIFYTAG = "New game request!"
    fun notify(context:Context, message:String, number:Int){
        val intent = Intent(context,MainActivity::class.java)
        val builder = NotificationCompat.Builder(context)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New Request")
            .setContentText(message)
            .setNumber(number)
            .setSmallIcon(R.mipmap.tictac)
            .setContentIntent(PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ECLAIR) {
            notificationManager.notify(NOTIFYTAG, 0, builder.build())
        }else{
            notificationManager.notify(NOTIFYTAG.hashCode(), builder.build())
        }
    }
}