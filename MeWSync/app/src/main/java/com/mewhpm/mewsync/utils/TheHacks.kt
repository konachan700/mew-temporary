package com.mewhpm.mewsync.utils

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout

fun androidx.appcompat.app.AppCompatActivity.hackFixOrder(dl: DrawerLayout, containerResId: Int) {
    val decor = window.decorView as ViewGroup
    val child = decor.getChildAt(0)
    decor.removeView(child)
    val container = dl.findViewById(containerResId) as LinearLayout
    container.addView(child)
    //decor.addView(dl)
}