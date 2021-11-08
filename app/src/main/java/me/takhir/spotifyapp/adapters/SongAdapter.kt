package me.takhir.spotifyapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.list_item.view.*
import me.takhir.spotifyapp.R
import me.takhir.spotifyapp.data.entities.Song
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {

    override val differ = AsyncListDiffer<Song>(this, diffUtilCallback)

    override fun onBindViewHolder(holder: BaseSongAdapter.SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            setOnClickListener { clickCallback.invoke(song) }
        }
    }

}