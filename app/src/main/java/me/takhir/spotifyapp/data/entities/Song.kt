package me.takhir.spotifyapp.data.entities


data class Song(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val songUrl: String = ""
) {
    fun areContentsTheSame(newSong: Song) = mediaId == newSong.mediaId &&
            title == newSong.title &&
            subtitle == newSong.subtitle &&
            imageUrl == newSong.imageUrl &&
            songUrl == newSong.songUrl
}