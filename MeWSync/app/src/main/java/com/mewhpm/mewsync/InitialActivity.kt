package com.mewhpm.mewsync

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.ui.pinpad.PinCodeBaseDialogFragment
import com.mewhpm.mewsync.ui.pinpad.verifyPin
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.utils.VibroUtils
import kotlinx.android.synthetic.main.x00_empty.*
import kotlinx.android.synthetic.main.x00_pincode_fragment.*
import kotlinx.android.synthetic.main.x00_wait_fragment.view.*

class InitialActivity : AppCompatActivity() {
    private var _pinHash = ""

    private fun addNumber(number: String) {
        VibroUtils.vibrate(this, 100)
        val pin = StringBuilder()
            .append(_pinHash)
            .append(number)
            .append(CryptoUtils.getUniqueSalt())
            .append(number)
            .toString()
        _pinHash = CryptoUtils.sha256(pin)
        this@InitialActivity.pincodeBox1.append("\u25cf")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.x00_empty)

        this.waitDummy1.waitText1.text = getString(R.string.wait_for_keys_generating)
        CryptoUtils.checkGenerated {
            this@InitialActivity.runOnUiThread {
                this.waitDummy1.visibility = View.GONE

                setContentView(R.layout.x00_pincode_fragment)

                this@InitialActivity.button11.setOnClickListener { addNumber("1") }
                this@InitialActivity.button12.setOnClickListener { addNumber("2") }
                this@InitialActivity.button13.setOnClickListener { addNumber("3") }
                this@InitialActivity.button21.setOnClickListener { addNumber("4") }
                this@InitialActivity.button22.setOnClickListener { addNumber("5") }
                this@InitialActivity.button23.setOnClickListener { addNumber("6") }
                this@InitialActivity.button31.setOnClickListener { addNumber("7") }
                this@InitialActivity.button32.setOnClickListener { addNumber("8") }
                this@InitialActivity.button33.setOnClickListener { addNumber("9") }
                this@InitialActivity.button42.setOnClickListener { addNumber("0") }
                this@InitialActivity.button41.setOnClickListener {
                    VibroUtils.vibrate(this, 100)
                    this@InitialActivity.pincodeBox1.text.clear()
                    _pinHash = PinCodeBaseDialogFragment.EMPTY
                }
                this@InitialActivity.button43.setOnClickListener {
                    VibroUtils.vibrate(this, 100)
                    if (_pinHash.contentEquals("")) {
                        this@InitialActivity.incorrectPinWarning.visibility = View.VISIBLE
                        this@InitialActivity.incorrectPinWarning.text = getString(R.string.pincode_cannot_be_empty)
                        return@setOnClickListener
                    }

                    val defaultDevice = KnownDevicesDao.getInstance(applicationContext.connectionSource).getDefault()
                    if (defaultDevice != null) {
                        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val retVal = CryptoUtils.verifyPinCode(pref, _pinHash, defaultDevice)
                        if (retVal) {
                            val act2 = Intent(applicationContext, DeviceActivity::class.java)
                            act2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            act2.putExtra("pincode", "")
                            act2.putExtra("dev_mac", defaultDevice.mac)
                            act2.putExtra("dev_name", defaultDevice.name)

                            DeviceActivity.currentDeviceMac = defaultDevice.mac
                            finish()
                            startActivityForResult(act2, 0)

                            this@InitialActivity.incorrectPinWarning.visibility = View.INVISIBLE
                        } else {
                            this@InitialActivity.incorrectPinWarning.text = "Incorrect pin-code, please try again"
                            this@InitialActivity.incorrectPinWarning.visibility = View.VISIBLE
                        }
                    } else {
                        this@InitialActivity.incorrectPinWarning.visibility = View.VISIBLE
                        this@InitialActivity.incorrectPinWarning.text = "Strange error while request a device."
                    }
                }
            }
        }
    }
}