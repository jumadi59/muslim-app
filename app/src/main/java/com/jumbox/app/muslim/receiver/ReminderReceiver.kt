package com.jumbox.app.muslim.receiver

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.data.Repository
import com.jumbox.app.muslim.data.db.AppDatabase
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.ui.prayer.PrayerTimeActivity
import com.jumbox.app.muslim.ui.prayer.RingAdzanActivity
import com.jumbox.app.muslim.utils.PrayerUtil
import com.jumbox.app.muslim.utils.dateFormat
import com.jumbox.app.muslim.utils.nameResource
import com.jumbox.app.muslim.vo.Prayer
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


/**
 * Created by Jumadi Janjaya date on 14/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class ReminderReceiver : DaggerBroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "Notification Adzan"

        private const val ALARM_ID = 1010
        const val ACTION_REMINDER = "com.jumbox.app.islamic.ACTION_REMINDER"

        private const val EXTRA_PRAYER_TIME = "extra_time"
        private const val EXTRA_PRAYER_ID = "extra_id"

        fun enableReminder(context: Context, timeBefore: Long = Date().time) {
            val db = AppDatabase.newInstance(context)
            val preference = Preference(context)
            val prayerUtil = PrayerUtil(preference)
            val now = Date().time
            val alarmTime = preference.alarmTimeOut * 60000

            GlobalScope.launch(Dispatchers.IO) {
                getNextPrayer(db, preference, timeBefore)?.let { prayer ->
                    prayerUtil.correctionTimingPrayer(prayer).let {
                        if (alarmTime > 0 && (it.time - alarmTime) > now && (it.name != "sunrise" && it.name != "dhuha" && it.name != "imsak"))
                            enableReminder(context, it.id, it.time - alarmTime)
                        else enableReminder(context, it.id, it.time)
                    }
                }
            }
        }

        private suspend fun getNextPrayer(db: AppDatabase, preference: Preference, timeBefore: Long) : Prayer? {
            db.prayerDao().getNext(timeBefore)?.let { prayer ->
                if (preference.notifications.find { it == prayer.name } != null)
                    return prayer
                else getNextPrayer(db, preference, prayer.time)
            }
            return null
        }

        private fun enableReminder(context: Context, idPrayer: Int, time: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.time = Date(time)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(context, ALARM_ID, Intent(context, ReminderReceiver::class.java).apply {
                    action = ACTION_REMINDER
                    putExtra(EXTRA_PRAYER_ID, idPrayer)
                    putExtra(EXTRA_PRAYER_TIME, time)
                },
                    PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(context, ALARM_ID, Intent(context, ReminderReceiver::class.java).apply {
                        action = ACTION_REMINDER
                        putExtra(EXTRA_PRAYER_ID, idPrayer)
                        putExtra(EXTRA_PRAYER_TIME, time)
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }

            Log.d("ReminderReceiver", "enableReminder ${dateFormat("HH:mm:ss", time)} isReminder ${isReminder(context)}")

            context.packageManager.setComponentEnabledSetting(
                ComponentName(context, ReminderReceiver::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        }

        fun isReminder(context: Context) : Boolean {
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(context, ALARM_ID,
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_MUTABLE
            ) else PendingIntent.getBroadcast(context, ALARM_ID,
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            return pendingIntent != null
        }

        private fun cancelReminder(context: Context) {
            if (!isReminder(context)) return

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(
                context, ALARM_ID, Intent(
                    context,
                    ReminderReceiver::class.java
                ), PendingIntent.FLAG_MUTABLE
            ) else PendingIntent.getBroadcast(
                context, ALARM_ID, Intent(
                    context,
                    ReminderReceiver::class.java
                ), PendingIntent.FLAG_CANCEL_CURRENT
            )
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)

            context.packageManager.setComponentEnabledSetting(
                ComponentName(context, ReminderReceiver::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        fun updateAlarm(context: Context) {
            cancelReminder(context)
            enableReminder(context)
        }

    }

    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var preference: Preference
    @Inject
    lateinit var repository: Repository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val prayerUtil = PrayerUtil(preference)

        if (intent.action == ACTION_REMINDER) {
            val id = intent.getIntExtra(EXTRA_PRAYER_ID, 0)
            val time = intent.getLongExtra(EXTRA_PRAYER_TIME, 0L)
            if (time > 0L && id > 0) {
                GlobalScope.launch(Dispatchers.IO) {
                    db.prayerDao().get(id)?.let { prayer ->
                        prayerUtil.correctionTimingPrayer(prayer).let {
                            Log.d("ReminderReceiver", it.name)
                            if (it.time == time) {
                                alarmAdhan(context, it)
                                nextReminder(context, prayer.time)
                            } else {
                                val title = dateFormat("HH:mm", time)
                                val body = if (it.name == "imsak" || it.name == "sunrise")
                                    context.getString(R.string.alarm_timed_before, preference.alarmTimeOut.toString(),
                                        context.getString(it.name.nameResource(R.string::class.java)))
                                else context.getString(
                                    R.string.alarm_timed_before_adzan,
                                    preference.alarmTimeOut.toString(),
                                    context.getString(it.name.nameResource(R.string::class.java))
                                )
                                notify(context, title, body, prayer.time,
                                    Uri.parse("android.resource://"
                                            + context.packageName + "/" + if (prayer.name == "fajr") R.raw.fajr else R.raw.normal))
                                nextReminder(context, time)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun alarmAdhan(context: Context, prayer: Prayer) {
        if (!isAppIsInBackground(context) && prayer.type == "adzan") {
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_REMINDER).putExtra(BaseActivity.EXTRA_PRAYER_NOW, prayer.time))
            nextReminder(context, prayer.time)
        } else {
            val title = dateFormat("HH:mm", prayer.time)
            val body = if (prayer.name == "imsak" || prayer.name == "sunrise")
                context.getString(R.string.notification_body, context.getString(prayer.name.nameResource(R.string::class.java)))
            else {
                context.startActivity(
                    Intent(context, RingAdzanActivity::class.java)
                        .setAction(ACTION_REMINDER)
                        .putExtra(BaseActivity.EXTRA_PRAYER_NOW, prayer.time)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                context.getString(
                    R.string.notification_body_adzan, context.getString(
                        prayer.name.nameResource(R.string::class.java)
                    )
                )
            }
            notify(context, title, body, prayer.time,
                Uri.parse("android.resource://"
                        + context.packageName + "/" + if (prayer.name == "fajr") R.raw.fajr else R.raw.normal))
            nextReminder(context, prayer.time)
        }
    }

    private fun notify(
        context: Context,
        title: String,
        msg: String,
        id: Long,
        sound: Uri,
    ) {
        val managerCompat = NotificationManagerCompat.from(context)
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, PrayerTimeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_MUTABLE
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg)).setSound(sound)
            .setAutoCancel(true).setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            managerCompat.createNotificationChannel(channel)
        }
        managerCompat.notify(id.toInt(), builder.build())
    }

    private fun nextReminder(context: Context, time: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val prayer = db.prayerDao().getNext(time)
            if (prayer == null) {
                repository.loadApiPrayer(dateFormat("yyyy-MM-dd", Date().time + 1000 * 60 * 60 * 24))?.let {
                    enableReminder(context, time)
                }
            } else enableReminder(context, time)
        }
    }

    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo: ActivityManager.RunningAppProcessInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess: String in processInfo.pkgList) {
                    if ((activeProcess == context.packageName)) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }
}