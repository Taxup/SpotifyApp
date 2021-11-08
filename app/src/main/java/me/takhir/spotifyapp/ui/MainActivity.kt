package me.takhir.spotifyapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import me.takhir.spotifyapp.R
import me.takhir.spotifyapp.adapters.SwipeSongAdapter
import me.takhir.spotifyapp.data.entities.Song
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        vpSong.adapter = swipeSongAdapter
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
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
                                swipeSongAdapter.songs = songs
                                if (songs.isNotEmpty()) {
                                    glide.load((currentPlayingSong ?: songs[0]).imageUrl).into(ivCurSongImage)
                                }
                                switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> Unit
                    }
                }
            })
            currentSong.observe(this@MainActivity, {
                if (it == null) return@observe

                currentPlayingSong = it.toSong()
                glide.load(currentPlayingSong?.imageUrl).into(ivCurSongImage)
                switchViewPagerToCurrentSong(currentPlayingSong ?: return@observe)
            })
        }
    }
}