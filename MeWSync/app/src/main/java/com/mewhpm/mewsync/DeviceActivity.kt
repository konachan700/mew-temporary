package com.mewhpm.mewsync

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.LayoutInflaterCompat
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import kotlinx.android.synthetic.main.x02_activity_device.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import com.mikepenz.iconics.context.IconicsContextWrapper
import ru.ztrap.iconics.kt.wrapByIconics
import android.view.MenuInflater
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil


class DeviceActivity : Activity() {
    private val menuIconMap : HashMap<Int, Drawable> = HashMap()

    private fun generateIconDrawable(icon: GoogleMaterial.Icon) : Drawable {
        return Icon.createWithBitmap(
            IconicsDrawable(this@DeviceActivity.applicationContext)
                .icon(icon).sizeDp(24)
                .color(ContextCompat.getColor(this@DeviceActivity.applicationContext, R.color.colorBrandDark1))
                .toBitmap()).loadDrawable(this@DeviceActivity.applicationContext)
    }

    private fun generateIcon(icon: GoogleMaterial.Icon, colorResId: Int) : Icon {
        return Icon.createWithBitmap(
            IconicsDrawable(this@DeviceActivity.applicationContext)
                .icon(icon).sizeDp(32)
                .color(ContextCompat.getColor(this@DeviceActivity.applicationContext, colorResId))
                .toBitmap())
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.x02_activity_device)
        menuIconMap.putAll(hashMapOf(
            R.id.menuItemPasswords to generateIconDrawable(GoogleMaterial.Icon.gmd_vpn_key)
        ))


        with (this.menuButton) {
            setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_apps, R.color.colorBrandYellow))
            onClick {
                this@DeviceActivity.drawerLayout1.openDrawer(GravityCompat.START)
            }
        }

        with (this.fragmentMenuButton) {
            setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_menu, R.color.colorWhite))
            onClick {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        IconicsMenuInflaterUtil.inflate(menuInflater, this, R.menu.device_navigation_view, menu)
//        menuIconMap.forEach {
//            val menuItem = menu.findItem(it.key)
//            menuItem?.icon = it.value
//        }
        return true
    }
}
