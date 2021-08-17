package com.jumbox.app.muslim.ui.quran

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseFragment
import com.jumbox.app.muslim.databinding.FragmentSurahBinding
import com.jumbox.app.muslim.utils.elevationAppBar

/**
 * A fragment representing a list of Items.
 */
class SurahFragment : BaseFragment<FragmentSurahBinding,  QuranViewModel>() {

    val surahAdapter: SurahAdapter by lazy { SurahAdapter {
        startActivity(Intent(requireContext(), QuranActivity::class.java).putExtra(QuranActivity.EXTRA_SURAH, it))
    } }

    override fun getLayoutId() = R.layout.fragment_surah

    override fun getViewModelClass() = QuranViewModel::class

    override fun initView() {
        binding.rvList.elevationAppBar((requireActivity() as QuranListActivity).appbar())

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
            )
            val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            addItemDecoration(dividerItemDecoration)
            adapter = surahAdapter
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responseListSurah.observe(this) {
            surahAdapter.submitList(it)
        }
    }
}