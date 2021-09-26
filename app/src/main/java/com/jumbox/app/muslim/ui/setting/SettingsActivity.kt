package com.jumbox.app.muslim.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.android.billingclient.api.*
import com.jumbox.app.muslim.BuildConfig
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BasePreferenceActivity
import com.jumbox.app.muslim.databinding.PreferenceSettingsBinding
import com.jumbox.app.muslim.ui.about.AboutActivity
import com.jumbox.app.muslim.utils.elevationAppBar
import com.jumbox.app.muslim.utils.gone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : BasePreferenceActivity<PreferenceSettingsBinding>(),
    PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient

    override fun getLayoutId() = R.layout.preference_settings

    override fun initView() {
        setSupportActionBar(binding.layoutAppbar.toolbar)
        binding.nestedScrollView.elevationAppBar(binding.layoutAppbar.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }

        binding.alarmAdzan.root.setOnClickListener {
            startActivity(Intent(this, AlaramBeforePreference::class.java))
        }

        binding.corrountAdzan.root.setOnClickListener {
            startActivity(Intent(this, TimingCorrectionPreference::class.java))
        }

        binding.about.root.setOnClickListener  {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.rate.root.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
            })
        }

        binding.more.root.setOnClickListener  {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/dev?id=${BuildConfig.DEVELOPER_ID}")
            })
        }

        binding.donate.root.gone()
        binding.donate.root.setOnClickListener {
            connect()
        }
    }

    private fun connect() {
        billingClient = BillingClient
            .newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    CoroutineScope(Dispatchers.IO).launch { queryOneTimeProducts() }
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private suspend fun queryOneTimeProducts() {
        val skuListToQuery = ArrayList<String>()
        skuListToQuery.add("donate")
        val params = SkuDetailsParams.newBuilder()
        params
            .setSkusList(skuListToQuery)
            .setType(BillingClient.SkuType.INAPP)

        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }

        if (skuDetailsResult.skuDetailsList != null) {
            for (skuDetail in skuDetailsResult.skuDetailsList!!) {
                Log.i("SettingActivity", skuDetail.toString())
                val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetail).build()
                val debugMessage = billingClient.launchBillingFlow(this, flowParams).debugMessage
                Log.i("SettingActivity", debugMessage)
            }
        } else {
            Log.i("SettingActivity", "No skus found from query")
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }
    }

    override fun onResume() {
        super.onResume()
        initData(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        binding.alarmAdzan.summary = if (preference.alarmTimeOut == 0)
            getString(R.string.alarm_shalat_now) else getString(
            R.string.alarm_before_time_salat,
            " ${preference.alarmTimeOut}"
        )
        binding.corrountAdzan.summary = preference.alarmCorrectionTime.joinToString(",")
        binding.donate.summary = "Berdonasi untuk para pengembang aplikasi"
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                CoroutineScope(Dispatchers.IO).launch { handlePurchase(purchase) }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }
}