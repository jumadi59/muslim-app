package com.jumbox.app.muslim.ui.quran

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseFragment
import com.jumbox.app.muslim.databinding.FragmentSurahBinding
import com.jumbox.app.muslim.utils.SwipeToDeleteCallback
import com.jumbox.app.muslim.utils.elevationAppBar
import com.jumbox.app.muslim.utils.gone
import com.jumbox.app.muslim.utils.messageDisplay


class BookmarkFragment : BaseFragment<FragmentSurahBinding, QuranViewModel>() {

    val bookmarkAdapter: BookmarkAdapter by lazy { BookmarkAdapter {
        startActivity(Intent(requireContext(), QuranActivity::class.java).putExtra(QuranActivity.EXTRA_BOOKMARK, it))
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
            adapter = bookmarkAdapter
        }

        ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                Log.d("BookmarkFragment", "position $position")
                val item = bookmarkAdapter.getItem(position)
                var isUndo = false
                bookmarkAdapter.removeItem(position)
                Snackbar.make(binding.root, R.string.message_delete_bookmark, Snackbar.LENGTH_LONG).setAction(R.string.undo) {
                    bookmarkAdapter.restoreItem(item, position)
                    binding.rvList.scrollToPosition(position)
                    isUndo = true
                }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (!isUndo && item != null) viewModel.deleteBookmark(item.number, item.verse)
                    }
                }).show()
            }
        }).attachToRecyclerView(binding.rvList)
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responseBookmarks.observe(this) {
            bookmarkAdapter.submitList(it)
            if (it.isEmpty()) {
                binding.layoutMsg.messageDisplay(getString(R.string.title_empty_bookmark), getString(R.string.message_empty_bookmark), iconId = R.drawable.ic_favorite_active)
                binding.layoutMsg.ivIcon.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorIconDark, requireActivity().theme))
            } else binding.layoutMsg.root.gone()
        }
    }

}