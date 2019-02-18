package com.mewhpm.mewsync

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.fragments.PasswordsRootFragment
import com.mewhpm.mewsync.ui.fragmentpages.getFragmentBook
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.x02_activity_device.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import ru.ztrap.iconics.kt.wrapByIconics
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.mewhpm.mewsync.utils.hackFixOrder
import kotlinx.android.synthetic.main.passwords_fragment_action_bar.view.*


class DeviceActivity : AppCompatActivity() {
    companion object {
        const val TAB_PASSWORDS_TREE_ROOT = "passwords_tree"
    }

    private val passwordFragment = PasswordsRootFragment()
    init {
        passwordFragment.onFragmentAppear = {f, me ->

        }
    }

    private fun generateIcon(icon: GoogleMaterial.Icon, colorResId: Int, size: Int = 28) : Icon {
        return Icon.createWithBitmap(
            IconicsDrawable(this@DeviceActivity.applicationContext)
                .icon(icon).sizeDp(size)
                .color(ContextCompat.getColor(this@DeviceActivity.applicationContext, colorResId))
                .toBitmap())
    }

    private fun onMenuClickSelector(menuItemResId : Int) {
        when (menuItemResId) {
            R.id.menuItemPasswords -> {
                this@DeviceActivity.getFragmentBook(R.id.fragment_holder_dev_1).showTopInGroup(TAB_PASSWORDS_TREE_ROOT) }
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
        setSupportActionBar(this.activity_device_toolbar as Toolbar)


//        val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val viewAb = inflator.inflate(R.layout.passwords_fragment_action_bar, null)
//        with (viewAb) {
//            menuButton.setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_apps, R.color.colorWhite))
//            onClick {
//                Log.e("-----", "viewAb onClick")
//                this@DeviceActivity.drawerLayout1.openDrawer(GravityCompat.START)
//            }
//        }
//
//        with (supportActionBar!!) {
//            setDisplayHomeAsUpEnabled(false)
//            setDisplayShowHomeEnabled (false)
//            setDisplayShowCustomEnabled(true)
//            setDisplayShowTitleEnabled(false)
//            customView = viewAb
//        }

        getFragmentBook(R.id.fragment_holder_dev_1).groupTopFragmentRequest = { group ->
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
//                this@DeviceActivity.hack_ShowOptionMenu()
//            }
//        }

        this.logoutIcon1.setImageIcon(generateIcon(GoogleMaterial.Icon.gmd_arrow_back, R.color.colorWhite, 20))
        logoutTextView1.setOnClickListener {
            KnownDevicesDao.getInstance(connectionSource).clearDefault()
            this@DeviceActivity.finish()
        }

        this.navView1.menu.findItem(R.id.menuItemPasswords).isChecked = true
        getFragmentBook(R.id.fragment_holder_dev_1).showTopInGroup(TAB_PASSWORDS_TREE_ROOT)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //Log.e("-----", "onPrepareOptionsMenu ${menu?.size()}")
//        if (menu != null && menu.size() > 0) {
//            //menu.clear()
//            menu.add(0, 0, 0, "1")
//        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.e("-----", "onCreateOptionsMenu ${menu?.size()}")
        return super.onCreateOptionsMenu(menu)
    }

}
