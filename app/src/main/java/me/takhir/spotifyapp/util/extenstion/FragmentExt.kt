package me.takhir.spotifyapp.util.extenstion

import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

fun Fragment.setupToolbar(
    toolbar: Toolbar,
    text: String? = null,
    @DrawableRes icon: Int? = null,
    onNavigationClickListener: () -> Unit = { activity?.onBackPressed() }
) {
    val activity = activity as AppCompatActivity?
    activity?.apply {
        setSupportActionBar(toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
        }
    }
    toolbar.title = text ?: ""
    icon?.apply { toolbar.setNavigationIcon(icon) }
    toolbar.setNavigationOnClickListener { onNavigationClickListener() }
}