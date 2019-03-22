package com.mewhpm.mewsync

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.mewhpm.mewsync.fragments.DevicesFragment
import com.mewhpm.mewsync.utils.eq
import com.mewhpm.mewsync.utils.toHexString
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import ru.ztrap.iconics.kt.setIconicsFactory
import ru.ztrap.iconics.kt.wrapByIconics


class MainActivity : AppCompatActivity() {
    private val _knownDevicesFragment = DevicesFragment()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.wrapByIconics())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutInflater.setIconicsFactory(delegate)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.x01_activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, _knownDevicesFragment,"DevicesFragment")
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    @ExperimentalUnsignedTypes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        try {
            if (data == null) {
                toast("Bad data from activity!").show()
                return
            }
            if (requestCode == 1) {
                if (!data.hasExtra(SimpleScannerActivity.DATA_ID)) {
                    toast("No any data from activity!").show()
                    return
                }

                val datab64 = data.getStringExtra(SimpleScannerActivity.DATA_ID)
                _knownDevicesFragment.qrCodeDataProcess(datab64)
            }
        } catch (e : Throwable) {
            toast("Bad QR-code!").show()
        }
    }
}
