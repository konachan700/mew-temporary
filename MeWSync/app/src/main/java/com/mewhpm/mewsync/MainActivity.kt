package com.mewhpm.mewsync

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mewhpm.mewsync.fragments.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import ru.ztrap.iconics.kt.setIconicsFactory
import ru.ztrap.iconics.kt.wrapByIconics

class MainActivity : AppCompatActivity(), FragmentCloseRequest {
    private val _bleSearchFragment: DeviceDiscoveryFragment = DeviceDiscoveryFragment()
    private val _knownDevicesFragment: KnownDevicesFragment = KnownDevicesFragment()
    private val _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.setIconicsFactory(delegate)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_holder, _knownDevicesFragment)
        transaction.commit()

        fab.setImageIcon(Icon.createWithBitmap(
            IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_refresh)
                .sizeDp(24)
                .color(Color.WHITE)
                .toBitmap()
        ))

        fab.setOnClickListener { view ->
            if (!_bluetoothAdapter.isEnabled) {
                alert("Bluetooth", "Bluetooth is disabled!") {
                    okButton {  }
                }.show()
            } else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_holder, _bleSearchFragment)
                transaction.commit()
                fab.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun close(source: Fragments) {
        when(source) {
            Fragments.DEVICE_DISCOVERY -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_holder, _knownDevicesFragment)
                transaction.commit()
                fab.show()
            }
            Fragments.KNOWN_DEVICES -> {

            }
        }
    }
}
