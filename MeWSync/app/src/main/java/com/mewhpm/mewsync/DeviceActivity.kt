package com.mewhpm.mewsync

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.x02_activity_device.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import ru.ztrap.iconics.kt.wrapByIconics
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mewhpm.mewsync.fragments.PasswordsRootFragment
import com.mewhpm.mewsync.ui.fragmentpages.getFragmentBook
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import java.lang.IllegalArgumentException


class DeviceActivity : AppCompatActivity() {
    companion object {
        const val TAB_PASSWORDS_TREE_ROOT = "passwords_tree"
    }

    val passwordFragment = PasswordsRootFragment()

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

    private fun onMenuClickSelector(menuItemResId : Int) {
        when (menuItemResId) {
            R.id.menuItemPasswords -> { this@DeviceActivity.getFragmentBook(R.id.fragment_holder_dev_1).showTopInGroup(TAB_PASSWORDS_TREE_ROOT) }
            R.id.menuSync -> {}
        }
    }

    private fun onMenuClickBase(menuItem : MenuItem) : Boolean {
        for (i in 0..(this@DeviceActivity.navView1.menu.size() - 1)) {
            this@DeviceActivity.navView1.menu.getItem(i).isChecked = false
        }
        menuItem.isChecked = true
        this@DeviceActivity.drawerLayout1.closeDrawers()
        onMenuClickSelector(menuItem.itemId)
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.x02_activity_device)
        this@DeviceActivity.getFragmentBook(R.id.fragment_holder_dev_1).groupTopFragmentRequest = { group ->
            when (group) {
                TAB_PASSWORDS_TREE_ROOT -> passwordFragment
                else -> throw IllegalArgumentException("Fragment group $group not exist")
            }
        }

        this.navView1.menu.clear()
        this.navView1.setNavigationItemSelectedListener { menuItem -> onMenuClickBase(menuItem) }
        IconicsMenuInflaterUtil.inflate(menuInflater, this, R.menu.device_navigation_view, this.navView1.menu)

//        with (this.menuButton) {
//            setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_apps, R.color.colorWhite))
//            onClick {
//                this@DeviceActivity.drawerLayout1.openDrawer(GravityCompat.START)
//            }
//        }
//
//        with (this.fragmentMenuButton) {
//            setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_more_horiz, R.color.colorWhite))
//            onClick {
//
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        IconicsMenuInflaterUtil.inflate(menuInflater, this, R.menu.device_navigation_view, menu)
        return true
    }
}
