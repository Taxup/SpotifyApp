package me.takhir.spotifyapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.takhir.spotifyapp.exoplayer.MusicService
import me.takhir.spotifyapp.exoplayer.MusicServiceConnection
import me.takhir.spotifyapp.exoplayer.currentPlaybackPosition
import me.takhir.spotifyapp.util.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private val _currentSongDuration = MutableLiveData<Long>()
    val currentSongDuration: LiveData<Long> = _currentSongDuration

    private val _currentPlayerPosition = MutableLiveData<Long>()
    val currentPlayerPosition: LiveData<Long> = _currentPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlaybackPosition ?: 0
                if (currentPlayerPosition.value != pos) {
                    _currentPlayerPosition.value = pos
                    _currentSongDuration.value = MusicService.currentSongDuration
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}