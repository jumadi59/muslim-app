package com.jumbox.app.muslim.ui.hadist

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityHadistListBinding
import com.jumbox.app.muslim.utils.*
import com.jumbox.app.muslim.vo.Status.*

class HadistListActivity : BaseActivity<ActivityHadistListBinding, HadistViewModel>() {

    private val hadistAdapter: HadistAdapter by lazy { HadistAdapter { kitab: String, id: Int ->
        startActivity(Intent(this, HadistActivity::class.java).putExtra("kitab", kitab).putExtra("id", id))
    } }

    override fun getLayoutId() = R.layout.activity_hadist_list

    override fun getViewModelClass() = HadistViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
        }

        binding.rvList.elevationAppBar(binding.appBar)

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@HadistListActivity, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration = DividerItemDecoration(
                    this@HadistListActivity,
                    DividerItemDecoration.VERTICAL
            )
            dividerItemDecoration.setDrawable(
                    AppCompatResources.getDrawable(
                            this@HadistListActivity,
                            R.drawable.divider
                    )!!
            )
            addItemDecoration(dividerItemDecoration)
            adapter = hadistAdapter
        }

        binding.etSearch.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                viewModel.search(binding.etSearch.text.toString())
                binding.etSearch.hideInput(view.windowToken)
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.search(binding.etSearch.text.toString())
                binding.etSearch.hideInput(view.windowToken)
                true
            } else {
                false
            }
        }
        binding.layoutMsg.messageDisplay(getString(R.string.title_cari_hadist), getString(R.string.description_search_hadist))
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.resposeSearch.observe(this) {
            when(it.status) {
                SUCCESS -> {
                    binding.progressBar.gone()
                    binding.rvList.visible()
                    hadistAdapter.submitList(it.data)
                }
                ERROR -> {
                    binding.progressBar.gone()
                    binding.rvList.gone()
                    when (it.statusCode) {
                        204 or 404 -> binding.layoutMsg.messageDisplay(getString(R.string.title_search_not_found), getString(R.string.description_error_server), R.drawable.ic_search)
                        0 -> binding.layoutMsg.messageDisplay(getString(R.string.title_error), getString(R.string.description_error), R.drawable.ic_signal) {
                            viewModel.retryHSearch()
                        }
                        else -> binding.layoutMsg.messageDisplay(it.message?:getString(R.string.title_error_server), it.message?:getString(R.string.description_error_server), R.drawable.ic_cloud) {
                            viewModel.retryHSearch()
                        }
                    }
                }
                LOADING ->  {
                    binding.progressBar.visible()
                    binding.layoutMsg.root.gone()
                    binding.rvList.gone()
                    hadistAdapter.submitList(null)
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