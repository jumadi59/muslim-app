package com.jumbox.app.muslim.ui.prayer

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.receiver.ReminderReceiver
import dagger.android.support.DaggerAppCompatActivity

class RingAdzanActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring_adzan)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent)  {
        if (intent.action == ReminderReceiver.ACTION_REMINDER) {
            intent.getLongExtra(BaseActivity.EXTRA_PRAYER_NOW, 0L).let {
                if (it > 0L) {
                    with(AdzanDialogFragment(it) { finish() }) {
                        isCancelable = false
                        show(supportFragmentManager, "adzan_dialog")
                    }
                } else finish()
            }
        }
    }
}