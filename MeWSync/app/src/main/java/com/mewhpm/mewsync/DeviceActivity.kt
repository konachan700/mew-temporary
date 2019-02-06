package com.mewhpm.mewsync

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_device.*

class DeviceActivity : Activity() {
    private val _iconMenu = Icon.createWithBitmap(
        IconicsDrawable(this.applicationContext)
            .icon(GoogleMaterial.Icon.gmd_menu).sizeDp(32).color(Color.WHITE).toBitmap())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        this.menuButton.setImageIcon(_iconMenu)


    }
}
