package me.takhir.spotifyapp.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import me.takhir.spotifyapp.R
import me.takhir.spotifyapp.adapters.SwipeSongAdapter
import me.takhir.spotifyapp.data.entities.Song
import me.takhir.spotifyapp.exoplayer.isPlaying
import me.takhir.spotifyapp.exoplayer.toSong
import me.takhir.spotifyapp.ui.viewmodels.MainViewModel
import me.takhir.spotifyapp.util.Status
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var currentPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        vpSong.adapter = swipeSongAdapter
        vpSong.isUserInputEnabled = false
        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true && currentPlayingSong != swipeSongAdapter.songs[position]) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    currentPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })
        ivPlayPause.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
        ivPlayNext.setOnClickListener { mainViewModel.skipToNextSong() }
        ivPlayPrev.setOnClickListener { mainViewModel.skipToPrevSong() }

        swipeSongAdapter.setOnItemClickListener {
            navHostFragment.findNavController().navigate(R.id.globalActionToSongFragment)
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.songFragment -> hideBottomBar()
                R.id.homeFragment -> showBottomBar()
                else -> showBottomBar()
            }
        }
    }

    private fun hideBottomBar() {
        bottomBar.isVisible = false
    }

    private fun showBottomBar() {
        bottomBar.isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOfFirst { song.mediaId == it.mediaId }
        if (newItemIndex != -1) {
            vpSong.currentItem = newItemIndex
            currentPlayingSong = song
        }
    }

    private fun setupViewModel() {
        mainViewModel.apply {
            mediaItems.observe(this@MainActivity, {
                it?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            result.data?.let { songs ->
                                bottomBar.isVisible = true

                                swipeSongAdapter.songs = songs
                                if (songs.isNotEmpty()) {
                                    glide.load((currentPlayingSong ?: songs[0]).imageUrl).into(ivCurSongImage)
                                }
                                switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> bottomBar.isVisible = false
                    }
                }
            })
            currentSong.observe(this@MainActivity, {
                if (it == null) return@observe

                currentPlayingSong = it.toSong()
                glide.load(currentPlayingSong?.imageUrl).into(ivCurSongImage)
                switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
            })
            playbackState.observe(this@MainActivity, {
                this@MainActivity.playbackState = it
                ivPlayPause.setImageResource(
                    if (it?.isPlaying == true) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play
                    }
                )
            })
            isConnected.observe(this@MainActivity, {
                it.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> Unit
                        Status.ERROR -> Snackbar.make(
                            rootLayout,
                            result.message ?: "Unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).show()
                        Status.LOADING -> Unit
                    }
                }
            })
            networkError.observe(this@MainActivity, {
                it.getContentIfNotHandled()?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> Unit
                        Status.ERROR -> Snackbar.make(
                            rootLayout,
                            result.message ?: "Unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).show()
                        Status.LOADING -> Unit
                    }
                }
            })
        }
    }
}