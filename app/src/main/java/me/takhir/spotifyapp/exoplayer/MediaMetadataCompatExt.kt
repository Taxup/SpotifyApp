package me.takhir.spotifyapp.exoplayer

import android.support.v4.media.MediaMetadataCompat
import me.takhir.spotifyapp.data.entities.Song

fun MediaMetadataCompat.toSong() = Song(
    mediaId = description.mediaId.toString(),
    title = description.title.toString(),
    subtitle = description.subtitle.toString(),
    imageUrl = description.iconUri.toString(),
    songUrl = description.mediaUri.toString()
)