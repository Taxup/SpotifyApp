package me.takhir.spotifyapp.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import me.takhir.spotifyapp.R
import me.takhir.spotifyapp.data.entities.Song
import me.takhir.spotifyapp.exoplayer.isPlaying
import me.takhir.spotifyapp.exoplayer.toSong
import me.takhir.spotifyapp.ui.viewmodels.MainViewModel
import me.takhir.spotifyapp.ui.viewmodels.SongViewModel
import me.takhir.spotifyapp.util.Status
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private val mainViewModel: MainViewModel by activityViewModels()
    private val songViewModel: SongViewModel by activityViewModels()

    private var currentPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekBar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()

        ivPlayPauseDetail.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setCurrentPlayerTime(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekBar = false
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }
        })

        ivSkip.setOnClickListener { mainViewModel.skipToNextSong() }
        ivSkipPrevious.setOnClickListener { mainViewModel.skipToPrevSong() }
    }

    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun setupViewModel() {
        mainViewModel.apply {
            mediaItems.observe(viewLifecycleOwner, {
                it?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            result.data?.let { songs ->
                                if (currentPlayingSong == null && songs.isNotEmpty()) {
                                    currentPlayingSong = songs[0]
                                    updateTitleAndSongImage(songs[0])
                                }

                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> Unit
                    }
                }
            })
            currentSong.observe(viewLifecycleOwner, {
                if (it == null) return@observe

                currentPlayingSong = it.toSong()
                updateTitleAndSongImage(currentPlayingSong!!)
            })
            playbackState.observe(viewLifecycleOwner, {
                this@SongFragment.playbackState = it
                ivPlayPauseDetail.setImageResource(
                    if (it?.isPlaying == true) R.drawable.ic_round_pause_24
                    else R.drawable.ic_play
                )
                seekBar.progress = it?.position?.toInt() ?: 0
            })
        }
        songViewModel.apply {
            currentPlayerPosition.observe(viewLifecycleOwner, {
                if (shouldUpdateSeekBar) {
                    seekBar.progress = it.toInt()
                    setCurrentPlayerTime(it)
                }
            })
            currentSongDuration.observe(viewLifecycleOwner, {
                seekBar.max = it.toInt()
                setDurationPlayerTime(it)
            })
        }
    }

    private fun setDurationPlayerTime(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.ROOT)
        tvSongDuration.text = dateFormat.format(ms)
    }

    private fun setCurrentPlayerTime(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.ROOT)
        tvCurTime.text = dateFormat.format(ms)
    }
}