package com.mewhpm.mewsync

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mewhpm.mewsync.fragments.DeviceMainFragment
import com.mewhpm.mewsync.fragments.DevicesFragment
import com.mewhpm.mewsync.fragments.PinCodeDialogFragment
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_main.*
import ru.ztrap.iconics.kt.setIconicsFactory
import ru.ztrap.iconics.kt.wrapByIconics


class MainActivity : AppCompatActivity() {
    private val _knownDevicesFragment = DevicesFragment()
    private val _pincodeFragment = PinCodeDialogFragment()


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.setIconicsFactory(delegate)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomView.menu.getItem(0).icon = Icon.createWithBitmap(
            IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_vpn_key).sizeDp(40).color(Color.WHITE)
                .toBitmap()).loadDrawable(this)

        bottomView.menu.getItem(1).icon = Icon.createWithBitmap(
            IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_contacts).sizeDp(40).color(Color.WHITE)
                .toBitmap()).loadDrawable(this)

        bottomView.menu.getItem(2).icon = Icon.createWithBitmap(
            IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_settings).sizeDp(40).color(Color.WHITE)
                .toBitmap()).loadDrawable(this)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_holder, _knownDevicesFragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
