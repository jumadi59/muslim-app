package com.jumbox.app.muslim.ui.hadist

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityHadistBinding
import com.jumbox.app.muslim.utils.*
import com.jumbox.app.muslim.vo.Status.*

class HadistActivity : BaseActivity<ActivityHadistBinding, HadistViewModel>() {

    override fun getLayoutId() = R.layout.activity_hadist

    override fun getViewModelClass() = HadistViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        binding.nestedScrollView.elevationAppBar(binding.appBar)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding.typeDoc.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY == 0)
                    binding.appBar.elevation = 0f.dpToPx().toFloat()
                else
                    binding.appBar.elevation = 5f.dpToPx().toFloat()

            }
        }
    }

    override fun handleIntent(intent: Intent?) {
        val kitab = intent?.getStringExtra("kitab")
        val id = intent?.getIntExtra("id", 0)?:0
        if (!kitab.isNullOrEmpty() && id > 0) {
            binding.tvName.text = kitab.replace("_", " ")
            binding.tvNumber.text = getString(R.string.number, " $id")
            viewModel.hadist(kitab, id)
        } else {
            binding.layoutMsg.messageDisplay(getString(R.string.title_not_found, "Hadist"), getString(R.string.description_error_server), hideIcon = true)
            binding.nestedScrollView.gone()
            binding.progressBar.gone()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

        viewModel.resposeHadist.observe(this) {
            when(it.status) {
                SUCCESS -> {
                    binding.hadist = it.data
                    if (it.data?.arabic?.isEmpty() == true) {
                        binding.typeDoc.loadData(it.data.translation,"text/html", "UTF-8")
                        binding.typeDoc.visible()
                    } else
                        binding.nestedScrollView.visible()

                    binding.progressBar.gone()
                }
                ERROR -> {
                    binding.progressBar.gone()
                    binding.nestedScrollView.gone()
                    when (it.statusCode) {
                        204 -> binding.layoutMsg.messageDisplay(getString(R.string.title_not_found, "Hadist"), getString(R.string.title_not_found, "Hadist"), hideIcon = true)
                        0 -> binding.layoutMsg.messageDisplay(getString(R.string.title_error), getString(R.string.description_error), R.drawable.ic_signal) {
                            viewModel.retryHadist()
                        }
                        else -> binding.layoutMsg.messageDisplay(it.message?:getString(R.string.title_error_server), it.message?:getString(R.string.description_error_server), R.drawable.ic_cloud) {
                            viewModel.retryHadist()
                        }
                    }
                }
                LOADING -> {
                    binding.progressBar.visible()
                    binding.layoutMsg.root.gone()
                    binding.nestedScrollView.gone()
                }

            }
        }
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

}