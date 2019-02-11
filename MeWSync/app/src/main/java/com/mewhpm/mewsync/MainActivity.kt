package com.mewhpm.mewsync

import android.content.Context
import android.os.Bundle
import android.provider.Contacts
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.Utils.CryptoUtils
import com.mewhpm.mewsync.fragments.DevicesFragment
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.wait_fragment.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast
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
        setContentView(R.layout.activity_main)

        this.waitDummy1.waitText1.text = getString(R.string.wait_for_keys_generating)
        CryptoUtils.checkGenerated {
            this@MainActivity.runOnUiThread {
                this.waitDummy1.visibility = View.GONE
                this.bottomView.visibility = View.VISIBLE
                this.fragment_holder.visibility = View.VISIBLE

                bottomView.addIcon(GoogleMaterial.Icon.gmd_bluetooth_connected, "MeW Devices") {
                    showTab(_knownDevicesFragment)
                }

                bottomView.addIcon(GoogleMaterial.Icon.gmd_settings, "Settings") {
                    toast("test2")
                }

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
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
