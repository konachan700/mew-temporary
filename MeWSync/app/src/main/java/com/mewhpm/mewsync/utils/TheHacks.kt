package com.mewhpm.mewsync.utils

import android.content.res.ColorStateList
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.core.content.ContextCompat
import com.mewhpm.mewsync.R
import kotlinx.android.synthetic.main.x02_activity_device.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor

fun androidx.appcompat.app.AppCompatActivity.fixPaddingTopForNavigationView() {
    val sizeRes = resources.getIdentifier("status_bar_height", "dimen", "android")
    val res = if (sizeRes > 0) resources.getDimensionPixelSize(sizeRes) else 0
    this.navView1.setPadding(this.navView1.paddingLeft, res, this.navView1.paddingRight, this.navView1.paddingBottom)
}

private fun <T> reflectionGetItem(obj: Any, field: String, clazz: Class<T>) : T {
    val rfield = obj::class.java.getDeclaredField(field)
    rfield.isAccessible = true
    return rfield.get(obj) as T
}

// SearchView is not a simple-styleable element. I don't want to dig android sources for searching, what styles it can be used...
fun androidx.appcompat.app.AppCompatActivity.fixColorOfSearchBar(menu: Menu, itemResId: Int) {
    try {
        val sView = menu.findItem(itemResId).actionView as SearchView
        reflectionGetItem(sView, "mSearchButton", ImageView::class.java).imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorWhite))
        reflectionGetItem(sView, "mCloseButton", ImageView::class.java).imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorWhite))
        reflectionGetItem(sView, "mGoButton", ImageView::class.java).imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorWhite))
        reflectionGetItem(sView, "mVoiceButton", ImageView::class.java).imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorWhite))
        reflectionGetItem(sView, "mSearchSrcTextView", AutoCompleteTextView::class.java).textColor =
            ContextCompat.getColor(this, R.color.colorWhite)
    } catch (e: Throwable) {
        Toast.makeText(this, "Can't use reflection for paint the search bar...", Toast.LENGTH_SHORT).show()
    }
}

fun androidx.appcompat.widget.Toolbar.setOnLogoClickEvent(ev : () -> Unit) {
    try {
        reflectionGetItem(this, "mLogoView", ImageView::class.java).onClick { ev.invoke() }
    } catch (e: Throwable) {
        Log.e("onLogoClickEvent", "Can't use reflection for set action for the toolbar logo.\n\tERROR: ${e.message}")
    }
}


