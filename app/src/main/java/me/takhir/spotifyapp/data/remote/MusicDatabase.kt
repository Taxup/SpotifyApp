package me.takhir.spotifyapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.takhir.spotifyapp.data.entities.Song
import me.takhir.spotifyapp.util.Constants.SONG_COLLECTION


class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }   catch (e: Exception) {
            emptyList()
        }
    }

}