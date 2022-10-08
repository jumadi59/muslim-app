package com.jumbox.app.muslim.ui.asmaulhusna

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.core.content.FileProvider
import androidx.core.graphics.applyCanvas
import com.jumbox.app.muslim.BuildConfig
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityAsmaulHusnaBinding
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.yuyakaido.android.cardstackview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class AsmaulHusnaActivity : BaseActivity<ActivityAsmaulHusnaBinding, MainViewModel>() {

    private val stackCardAdapter: StackCardAdapter by lazy { StackCardAdapter() }
    private val manager by lazy { CardStackLayoutManager(this) }

    lateinit var mediaPlayer: MediaPlayer
    private var isPaused = false
    private var isCompletion = false
    private var starPlay = false
    private val handler = Handler(Looper.getMainLooper())

    override fun getLayoutId() = R.layout.activity_asmaul_husna

    override fun getViewModelClass() = MainViewModel::class

    private fun loadAsmaulHusna(): MediaPlayer = MediaPlayer.create(this, R.raw.asmaulhusna_hijjaz).apply {
        try {
            prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(true)
            it.setTitle(R.string.title_asmaul_husna)
        }
        binding.cardStackView.apply {
            manager.setStackFrom(StackFrom.Top)
            manager.setVisibleCount(4)
            manager.setTranslationInterval(16f)
            manager.setSwipeAnimationSetting(
                SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build())
            layoutManager = manager
            adapter = stackCardAdapter
        }
        mediaPlayer = loadAsmaulHusna()

        binding.btnPlay.setOnClickListener {
            when {
                !isCompletion-> {
                    if (starPlay) prev() else {
                        play()
                        playPlayer()
                    }
                }
                mediaPlayer.isPlaying && !isPaused -> pausePlayer()
                isPaused && !mediaPlayer.isPlaying -> playPlayer()
            }
        }

        binding.btnOk.setOnClickListener {
            if (starPlay && !isCompletion) when {
                mediaPlayer.isPlaying && !isPaused -> pausePlayer()
                isPaused && !mediaPlayer.isPlaying -> playPlayer()
            }
            else flip()
        }

        binding.btnShare.setOnClickListener {
            if (starPlay && !isCompletion) next() else {
                val file = saveBitmap(binding.cardStackView.run {
                    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
                        translate(-scrollX.toFloat(), -scrollY.toFloat())
                        if (background !=null)
                            background.draw(this)
                        else drawColor(Color.parseColor("#FFFFFF"))
                        draw(this)
                    }
                })
                file?.let { f -> shareIt(f) }
            }
        }

        mediaPlayer.setOnCompletionListener {
            closePlayer()
        }

    }

    private fun saveBitmap(bitmap: Bitmap) : File? {
        val dir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/screenshots")
        if (!dir.exists()) dir.mkdirs()
        val imageFile = File(dir.absolutePath, "/screenshot.jpg")
        val fos: FileOutputStream
        return try {
            fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            imageFile
        } catch (e: FileNotFoundException) {
            Log.e("saveBitmap", e.message, e)
            null
        } catch (e: IOException) {
            Log.e("saveBitmap", e.message, e)
            null
        }
    }

    private fun shareIt(file: File) {
        val uri =  FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun flip() {
        if (manager.topView is EasyFlipView) {
            val card = manager.topView as EasyFlipView
            card.flipTheView()
            if (card.isFrontSide) {
                manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
                binding.btnOk.setImageResource(R.drawable.ic_book)
                binding.btnPlay.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                binding.btnShare.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            }
            else {
                manager.setSwipeableMethod(SwipeableMethod.None)
                binding.btnOk.setImageResource(R.drawable.ic_baseline_clear_24)
                binding.btnPlay.animate().scaleX(0f).scaleY(0f).setDuration(200).start()
                binding.btnShare.animate().scaleX(0f).scaleY(0f).setDuration(200).start()
            }
        }
    }

    private fun play() {
        Log.d("AsmaulHusna", "start play")
        if (isCompletion) {
            starPlay = false
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            binding.btnOk.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
            binding.btnPlay.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
            binding.btnShare.animate().scaleX(0f).scaleY(0f).setDuration(150).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    binding.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    binding.btnOk.setImageResource(R.drawable.ic_book)
                    binding.btnShare.setImageResource(R.drawable.ic_share)

                    binding.btnPlay.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                    binding.btnOk.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                    binding.btnShare.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}

            }).start()
        } else {
            starPlay = true
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_clear_24)
            binding.btnOk.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
            binding.btnPlay.animate().scaleX(0f).scaleY(0f).setDuration(150).start()
            binding.btnShare.animate().scaleX(0f).scaleY(0f).setDuration(150).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    binding.btnPlay.setImageResource(R.drawable.ic_baseline_skip_previous_24)
                    binding.btnShare.setImageResource(R.drawable.ic_baseline_skip_next_24)

                    binding.btnPlay.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                    binding.btnOk.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                    binding.btnShare.animate().scaleX(1f).scaleY(1f).setDuration(150).setListener(null).start()
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}

            }).start()
        }
    }

    private fun prev() {
        if (manager.topPosition > 0) {
            stackCardAdapter.currentList[manager.topPosition-1].let {
                mediaPlayer.seekTo(it.currentTime.toInt())
                binding.cardStackView.rewind()
            }
        }
    }

    private fun next() {
        if (manager.topPosition < stackCardAdapter.itemCount) {
            stackCardAdapter.currentList[manager.topPosition].let {
                mediaPlayer.seekTo(it.currentTime.toInt())
                binding.cardStackView.swipe()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.names()
        viewModel.responseNames.observe(this) {
            stackCardAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPaused && mediaPlayer.currentPosition > 0) playPlayer()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) pausePlayer()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                if (starPlay) {
                    closePlayer()
                } else onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun pausePlayer() {
        binding.btnOk.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        Log.d("AsmaulHusna", "pause")
        isPaused = true
        mediaPlayer.pause()
    }

    private fun playPlayer() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.btnOk.setImageResource(R.drawable.ic_baseline_pause_24)
        Log.d("AsmaulHusna", "play")

        if (mediaPlayer.currentPosition == 0)
            stackCardAdapter.submitList(ArrayList(stackCardAdapter.currentList))
        else {
            if (manager.topPosition > 0) {
                stackCardAdapter.currentList[manager.topPosition-1].let {
                    mediaPlayer.seekTo(it.currentTime.toInt())
                }
            }
        }

        mediaPlayer.start()
        isPaused = false
        handler.postDelayed(runnable, 100)
    }

    private fun closePlayer() {
        isCompletion = true
        play()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer = loadAsmaulHusna()
        isCompletion = false
    }

    private val runnable = object : Runnable{
        override fun run() {
            if (manager.topPosition < stackCardAdapter.itemCount) {
                stackCardAdapter.currentList[manager.topPosition].let {
                    if (mediaPlayer.currentPosition > it.currentTime)
                        binding.cardStackView.swipe()
                }
            }
            handler.postDelayed(this, 100)
        }
    }
}