package com.jumbox.app.muslim.ui.quran

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityQuranBinding
import com.jumbox.app.muslim.utils.*
import com.jumbox.app.muslim.vo.Bookmark
import com.jumbox.app.muslim.vo.Status.*
import com.jumbox.app.muslim.vo.Surah
import java.util.*

class QuranActivity : BaseActivity<ActivityQuranBinding, QuranViewModel>() {

    companion object {
        const val EXTRA_SURAH = "extra_surah"
        const val EXTRA_BOOKMARK = "extra_ayah"
    }

    private var surah: Surah? = null
    private var currentAya: Int? = null
    private var bottomSheetTafsir: BottomSheetTafsir? = null
    private val bottomSheetListSurah = BottomSheetListSurah {
        viewModel.loadSurah(it.number.toString())
        binding.tvName.text = it.name
    }

    private val verseAdapter: VerseAdapter by lazy { VerseAdapter({
        val share = "${it.text} \n artinya: ${it.translations.id} (QS. ${surah!!.name}: ${it.number})"
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Search Ayah")
            putExtra(Intent.EXTRA_TEXT, share)
        })
    }, {
        if (it.bookmark) viewModel.deleteBookmark(surah!!.number, it.number)
        else viewModel.bookmark(Bookmark(0, surah!!.number, surah!!.name, surah!!.translations, surah!!.unicode, it.number))
        verseAdapter.updateVerse(it, it.copy(bookmark = !it.bookmark))
    }, {
        bottomSheetTafsir = BottomSheetTafsir(surah!!, it).apply { show(supportFragmentManager, "bottomSheetTafsir") }
    })
    }

    override fun getLayoutId() = R.layout.activity_quran

    override fun getViewModelClass() = QuranViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        binding.rvModeList.elevationAppBar(binding.appBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        binding.rvModeList.apply {
            layoutManager = LinearLayoutManager(
                    this@QuranActivity,
                    LinearLayoutManager.VERTICAL,
                    false
            )
            val dividerItemDecoration = DividerItemDecoration(this@QuranActivity, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(AppCompatResources.getDrawable(this@QuranActivity, R.drawable.divider)!!)
            addItemDecoration(dividerItemDecoration)
            adapter = verseAdapter
        }

        binding.tvName.setOnClickListener {
            bottomSheetListSurah.show(supportFragmentManager, "find_surah")
        }
    }

    override fun handleIntent(intent: Intent?) {
        intent?.getParcelableExtra<Surah>(EXTRA_SURAH)?.let {
            surah = it
            viewModel.loadSurah(it.number.toString())
            binding.tvName.text = it.name
            binding.tvType.text = String.format("${it.type} (%s)", it.place)
        }
        intent?.getParcelableExtra<Bookmark>(EXTRA_BOOKMARK)?.let {
            currentAya = it.verse
            viewModel.loadSurah(it.number.toString())
            binding.tvName.text = it.name
        }

        intent?.let {
            val appLinkAction = it.action
            val appLinkData: Uri? = intent.data
            if (Intent.ACTION_VIEW == appLinkAction) {
                appLinkData?.pathSegments?.also { paths ->
                    if (paths.size == 3) {
                        viewModel.loadSurah(paths[1])
                        currentAya = paths[2].toInt()
                    }
                }
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responseSurah.observe(this) {
            if (it != null) {
                surah = it
                binding.tvName.text = it.name
                binding.tvType.text = String.format("${it.type} (%s)", it.place)
                updateUI()
            }
        }
        viewModel.responseVerses.observe(this) {
            verseAdapter.submitList(it)
            updateUI()
        }
        
        viewModel.responseBookmark.observe(this) {
            verseAdapter.submitBookmark(it)
        }

        viewModel.responseTafsir.observe(this) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       // menuInflater.inflate(R.menu.menu_quran, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_switch_mode -> {
                if (binding.rvModeList.isVisible) {
                    item.setIcon(R.drawable.ic_quran)
                } else {
                    item.setIcon(R.drawable.ic_list)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI() {
        binding.rvModeList.visible()
        if (surah !=null) {
            verseAdapter.isTopBasmallah = surah!!.number in 2..8 || surah!!.number > 9
            verseAdapter.notifyDataSetChanged()
        }

        currentAya?.let { number ->
            val list = verseAdapter.currentList
            list.find { it.number == number }?.let { verse ->
                val index = list.indexOf(verse) + (if(verseAdapter.isTopBasmallah) 1 else 0)
                verseAdapter.currentFocus = number
                binding.rvModeList.scrollToPosition(index)
                verseAdapter.notifyItemChanged(index)
            }
        }
    }

}