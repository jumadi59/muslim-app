package com.jumbox.app.muslim.ui.prayer

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.binding.Binding
import com.jumbox.app.muslim.data.pref.Preference
import com.jumbox.app.muslim.databinding.FragmentDialogAdzanBinding
import com.jumbox.app.muslim.di.ViewModelFactory
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.jumbox.app.muslim.utils.isNight
import com.jumbox.app.muslim.utils.nameResource
import com.jumbox.app.muslim.vo.Prayer
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil


/**
 * Created by Jumadi Janjaya date on 23/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class AdzanDialogFragment(private val time: Long, private val callbackDismiss: (() -> Unit?)? = null) : DialogFragment(), MediaPlayer.OnCompletionListener, HasAndroidInjector {

    lateinit var binding: FragmentDialogAdzanBinding
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var factory: ViewModelFactory
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, factory).get(MainViewModel::class.java) }

    @Inject
    lateinit var preference: Preference
    private var mMediaPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null
    private var prayer: Prayer? = null

    private var mOriginalVolume = -1
    private var mAudioStream = AudioManager.STREAM_ALARM

    private val mOnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopAlarm()
            dismiss()
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            mMediaPlayer?.start()
        }
    }
    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                mOnAudioFocusChangeListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS)
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        requireDialog().window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_adzan, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().volumeControlStream = AudioManager.STREAM_ALARM
        viewModel.prayers()

        binding.close.setOnClickListener {
            stopAlarm()
            dismiss()
        }

        viewModel.responsePrayers.observe(this) {
            it.find { prayer -> prayer.time == time }?.let { prayer -> setPray(prayer) }
        }
    }

    private fun setPray(prayer: Prayer) {
        this.prayer = prayer
        binding.prayerName.text = getString(R.string.time_now_shalat, getString(prayer.name.nameResource(R.string::class.java)))
        Binding.dateTimeFormat(binding.prayerTime, prayer.time)
        uiBackground(prayer.backgroundColor, isNight())
        try {
            playAlarm(prayer.name)
        } catch (e: Exception) {
            Log.e("RingAlarmActivity", e.message, e)
        }
        val telephonyManager = requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    @Throws(Exception::class)
    private fun playAlarm(adzan: String) {
        val assetFileDescriptor = if (adzan == "fajr") {
            resources.openRawResourceFd(R.raw.fajr)
        } else {
            resources.openRawResourceFd(R.raw.normal)
        }
        mMediaPlayer = MediaPlayer().apply {
            setDataSource(assetFileDescriptor!!.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
            prepare()
            setOnCompletionListener(this@AdzanDialogFragment)
        }
        mAudioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager?.apply {
            mOriginalVolume = getStreamVolume(mAudioStream)
            if (mOriginalVolume == 0) {
                val volume: Int = ceil(getStreamMaxVolume(mAudioStream).toDouble() * (50f / 100.0)).toInt()
               setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val result: Int = requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build())
                if (result == AudioManager.AUDIOFOCUS_GAIN) {
                    mMediaPlayer?.start()
                }
            }
        }
    }

    private fun stopAlarm() {
        try {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
            mMediaPlayer = null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        if (mOriginalVolume != -1) {
            mAudioManager?.setStreamVolume(mAudioStream, mOriginalVolume, 0)
        }
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //mAudioManager?.abandonAudioFocusRequest(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_LOSS).build())
        //}
        val telephonyManager = requireActivity().getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        requireDialog().window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
        val assetFileDescriptor = when (prayer?.name) {
            "fajr" ->
                resources.openRawResourceFd(R.raw.dua_sehri)
            "maghrib" ->
                resources.openRawResourceFd(R.raw.dua_iftar)
            else -> null
        }

        if (assetFileDescriptor == null) {
            stopAlarm()
            return
        }

        try {
            mMediaPlayer = MediaPlayer().apply {
                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                setOnCompletionListener { stopAlarm() }
                prepare()
                GlobalScope.launch(Dispatchers.Default) {
                    delay(3000)
                    mMediaPlayer?.start()
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("RingAlarmActivity", e.message, e)
        }
    }

    override fun dismiss() {
        callbackDismiss?.invoke()
        super.dismiss()
    }

    private fun uiBackground(color: Int, isNight: Boolean) {
        binding.background.backgroundTintList = ColorStateList.valueOf(color)
        binding.imgTintMosque.imageTintList = ColorStateList.valueOf(color)

        if (isNight) {
            binding.imgLighting.setImageResource(R.drawable.ic_light)
            binding.imgSunMoon.setImageResource(R.drawable.ic_moon)
            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.WHITE)
            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.WHITE)
        } else {
            binding.imgLighting.setImageResource(R.drawable.ic_light2)
            binding.imgSunMoon.setImageResource(R.drawable.ic_sun)
            binding.imgSunMoon.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
            binding.imgLighting.imageTintList = ColorStateList.valueOf(Color.parseColor("#eeee8e"))
        }
    }
    override fun androidInjector(): AndroidInjector<Any> {
        return activityInjector
    }
}