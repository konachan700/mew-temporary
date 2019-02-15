package com.mewhpm.mewsync

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.fragments.DevicesFragment
import com.mewhpm.mewsync.ui.pinpad.verifyPin
import kotlinx.android.synthetic.main.x01_activity_main.*
import kotlinx.android.synthetic.main.x00_wait_fragment.view.*
import ru.ztrap.iconics.kt.setIconicsFactory
import ru.ztrap.iconics.kt.wrapByIconics


class MainActivity : AppCompatActivity() {
    private val _knownDevicesFragment = DevicesFragment()
    private var _lastFragment: Fragment? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.setIconicsFactory(delegate)
        super.onCreate(savedInstanceState)

        val defaultDevice = KnownDevicesDao.getInstance(applicationContext.connectionSource).getDefault()
        if (defaultDevice != null) {
            verifyPin { pin ->
                val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val retVal = CryptoUtils.verifyPinCode(pref, pin, defaultDevice)
                if (retVal) {
                    val act2 = Intent(applicationContext, DeviceActivity::class.java)
                    act2.putExtra("pincode", "")
                    act2.putExtra("dev_mac", defaultDevice.mac)
                    act2.putExtra("dev_name", defaultDevice.name)
                    startActivityForResult(act2, 0)
                    finish()
                }
                retVal
            }
        }

        setContentView(R.layout.x01_activity_main)

        this.waitDummy1.waitText1.text = getString(R.string.wait_for_keys_generating)
        CryptoUtils.checkGenerated {
            this@MainActivity.runOnUiThread {
                this.waitDummy1.visibility = View.GONE
                this.fragment_holder.visibility = View.VISIBLE

                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_holder, _knownDevicesFragment)
                transaction.commit()
            }
        }
    }

    private fun showTab(fragment: Fragment) {
        if (_lastFragment == fragment) return
        _lastFragment = fragment

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }
}
