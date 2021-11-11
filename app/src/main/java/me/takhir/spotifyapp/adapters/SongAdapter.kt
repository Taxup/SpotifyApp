package me.takhir.spotifyapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.list_item.view.*
import me.takhir.spotifyapp.R
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {

    override val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            //rootLayout.setBackgroundResource(if (song.selected) R.drawable.bg_active else R.drawable.bg_normal)
            glide.load(song.imageUrl).into(ivItemImage)
            setOnClickListener { clickCallback.invoke(song) }
        }
    }

}