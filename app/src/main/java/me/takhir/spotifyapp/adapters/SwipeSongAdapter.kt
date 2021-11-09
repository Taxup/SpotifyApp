package me.takhir.spotifyapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.list_item.view.*
import me.takhir.spotifyapp.R
import me.takhir.spotifyapp.data.entities.Song

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = "${song.title} - ${song.subtitle}"
            tvPrimary.text = text
            setOnClickListener { clickCallback.invoke(song) }
        }
    }

}