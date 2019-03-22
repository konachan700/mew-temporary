package com.mewhpm.mewsync.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.DeviceActivity
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.ui.recyclerview.impl.RecyclerViewDevicesImpl
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.utils.PinUtil
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.polidea.rxandroidble2.RxBleClient
import kotlinx.android.synthetic.main.x01_known_devices_fragment.view.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import com.mewhpm.mewsync.SimpleScannerActivity
import com.mewhpm.mewsync.utils.CryptoUtils.*
import com.mewhpm.mewsync.utils.eq
import com.mewhpm.mewsync.utils.toHexString
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast


class DevicesFragment : Fragment() {
    companion object {
        private const val ACCESS_COARSE_LOCATION = 43
        private const val ACCESS_CAMERA = 44
        private const val REQUEST_ENABLE_BT = 1
    }

    //var onQRCodeReaded : (bytes: ByteArray) -> Unit = {}

    private val _bleDiscoveryFragment = BleDiscoveryFragment()
    private var _rxBleClient: RxBleClient? = null

    private var _pinCreateFirstInput = true
    private var _pinCreateHash1 = ""
    private var _pinCreateHash2 = ""

    private var _pinHash = ""

    private var _pref: SharedPreferences? = null
    private var _rvDevices : RecyclerViewDevicesImpl? = null
    private var _view: View? = null
    private var dao : KnownDevicesDao? = null

    private fun showDiscoveryFragment() {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, _bleDiscoveryFragment, "DevicesFragment")
            .addToBackStack("bleDiscoveryFragment")
            .commit()
    }

    private fun msg(msg : String, ok: () -> Unit = {}) {
        alert(msg, "Bluetooth") {
            okButton { ok.invoke() }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_COARSE_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    showDiscoveryFragment()
                } else {
                    msg("Permissions not granted!")
                }
            }
            ACCESS_CAMERA -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    val act2 = Intent(this@DevicesFragment.requireContext(), SimpleScannerActivity::class.java)
                    this@DevicesFragment.activity!!.startActivityForResult(act2, 1)
                }
            }
        }
    }

    private fun refreshFromDb() {
        _rvDevices?.clear()
        getDao().getAll().forEach { _rvDevices?.add(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _pref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onResume() {
        super.onResume()
        _rxBleClient = RxBleClient.create(requireContext())
        refreshFromDb()
    }

    override fun onDetach() {
        super.onDetach()
        _pref = null
    }

    private fun getDao() : KnownDevicesDao {
        if (dao == null) dao = KnownDevicesDao.getInstance(this.requireContext().applicationContext.connectionSource)
        return dao!!
    }

    private fun createPincodeVerifyFragment() : PincodeFragment {
        val pincodeFragment = PincodeFragment()
        with (pincodeFragment) {
            onBackPressEvent = { activity!!.supportFragmentManager.popBackStack() }
            onClearAllEvent = { _pinHash = "" }
            onNumberAddedEvent = { _pinHash = PinUtil.addNumber(this@DevicesFragment.requireContext(), _pinHash, it) }
            onOKPressedEvent = {
                val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@DevicesFragment.requireContext())
                val retVal = CryptoUtils.verifyPinCode(pref, _pinHash, DeviceActivity.currentDeviceMac)
                if (retVal) {
                    val act2 = Intent(this@DevicesFragment.requireContext(), DeviceActivity::class.java)
                    act2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivityForResult(act2, 0)
                    activity!!.supportFragmentManager.popBackStack()
                    null
                } else
                    getString(R.string.incorrect_pin_code)
            }
        }
        return pincodeFragment
    }

    private fun createPincodeCreateFragment(dev : BleDevice, key: ByteArray? = null, iv: ByteArray? = null) : PincodeFragment {
        val pincodeFragment = PincodeFragment()
        with (pincodeFragment) {
            onBackPressEvent = { activity!!.supportFragmentManager.popBackStack() }
            onClearAllEvent = {
                if (_pinCreateFirstInput)
                    _pinCreateHash1 = ""
                else
                    _pinCreateHash2 = ""
            }
            onNumberAddedEvent = {
                if (_pinCreateFirstInput)
                    _pinCreateHash1 = PinUtil.addNumber(this@DevicesFragment.requireContext(), _pinCreateHash1, it)
                else
                    _pinCreateHash2 = PinUtil.addNumber(this@DevicesFragment.requireContext(), _pinCreateHash2, it)
            }
            onOKPressedEvent = { createPincode(pincodeFragment, dev, key, iv) }
        }
        return pincodeFragment
    }

    private fun createPincode(pincodeFragment: PincodeFragment, dev : BleDevice, key: ByteArray? = null, iv: ByteArray? = null) : String? {
        if (_pinCreateFirstInput) {
            if (_pinCreateHash1.isBlank()) return "Empty pincode not allowed!"

            pincodeFragment.clearInput()
            _pinCreateFirstInput = !_pinCreateFirstInput

            return "Please, repeat the pincode."
        } else {
            if (_pinCreateHash1.contentEquals(_pinCreateHash2)) {
                CryptoUtils.createPinCode(PreferenceManager.getDefaultSharedPreferences(context), _pinCreateHash2, dev)
                if (KnownDevicesDao.isDeviceZero(dev.mac)) {
                    this@DevicesFragment.alert (
                        title = "Create pincode",
                        message = "Pincode created.") {
                        okButton {}
                    }.show()
                } else {
                    getDao().addNew(dev)
                    if (key != null && iv != null) {
                        CryptoUtils.saveAesKeyAndIVToKeystore(key, iv, dev.mac, _pref)
                    }
                }
                activity!!.supportFragmentManager.popBackStack()
                return null
            }

            _pinCreateHash1 = ""
            _pinCreateHash2 = ""
            pincodeFragment.clearInput()
            _pinCreateFirstInput = !_pinCreateFirstInput

            return "Entered pincodes don't match"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.x01_known_devices_fragment, container, false)

        _rvDevices = _view!!.listRV1
        with (_rvDevices!!) {
            create()
            deleteEvent = { _, _, dev ->
                getDao().remove(dev)
                pinCodeRemove(dev, _pref)
                aes256DevKeyRemove(dev.mac, _pref)
                refreshFromDb()
            }
            setDefaultEvent = { _, _, dev ->
                getDao().setDefault(dev)
                refreshFromDb()
            }
            setDescriptionEvent = { dev, desc ->
                dev.text = desc
                getDao().save(dev)
            }
            deviceItemClickEvent = { dev ->
                DeviceActivity.currentDeviceMac = dev.mac
                val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@DevicesFragment.requireContext())
                if (!CryptoUtils.isPinPresent(pref, DeviceActivity.currentDeviceMac)) {
                    val devx = getDao().getByMac(KnownDevicesDao.ZERO_MAC)
                    if (devx != null) {
                        val pincodeFragment1 = createPincodeCreateFragment(devx)
                        pincodeFragment1.setTitle("Create your new pincode")
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_holder, pincodeFragment1, "DevicesFragment")
                            .addToBackStack("DevicesFragment")
                            .commit()
                    }
                } else {
                    val pincodeFragment1 = createPincodeVerifyFragment()
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_holder, pincodeFragment1, "DevicesFragment")
                        .addToBackStack("DevicesFragment")
                        .commit()
                }
            }
        }

        with (_bleDiscoveryFragment) {
            onExitEvent = { activity!!.supportFragmentManager.popBackStack() }
            onDeviceSelectEvent = { dev ->
                createDev(dev)
            }
        }

        with (_view!!.addNewBleDevBtn2) {
            setImageIcon(
                Icon.createWithBitmap(
                    IconicsDrawable(requireContext())
                        .icon(GoogleMaterial.Icon.gmd_bluetooth).sizeDp(24).color(Color.WHITE).toBitmap()
                )
            )
            setOnClickListener {
                when (_rxBleClient?.state) {
                    RxBleClient.State.BLUETOOTH_NOT_ENABLED -> {
                        msg("Bluetooth not enable! Please, enable it.") {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            activity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                        }
                    }
                    RxBleClient.State.BLUETOOTH_NOT_AVAILABLE -> msg("This device hasn't a BLE module")
                    RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
                        msg("Location service is disabled. Please, enable it.") {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                    }
                    RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED -> {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_LOCATION)
                    }
                    RxBleClient.State.READY -> {
                        showDiscoveryFragment()
                    }
                }
            }
        }

        with (_view!!.addNewBleDevBtn1) {
            setImageIcon(Icon.createWithBitmap(
                    IconicsDrawable(requireContext())
                        .icon(GoogleMaterial.Icon.gmd_add).sizeDp(64).color(Color.WHITE).toBitmap()
                    ))
            setOnClickListener {
                if (ContextCompat.checkSelfPermission(this@DevicesFragment.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), ACCESS_CAMERA)
                } else {
                    val act2 = Intent(this@DevicesFragment.requireContext(), SimpleScannerActivity::class.java)
                    this@DevicesFragment.activity!!.startActivityForResult(act2, 1)
                }
            }
        }
        refreshFromDb()
        return _view
    }

    @ExperimentalUnsignedTypes
    fun qrCodeDataProcess(qrdata64: String) {
        val dataBinary = Base64.decode(qrdata64, Base64.DEFAULT).toUByteArray()
        if ((dataBinary[0] eq 0x43u) && (dataBinary[1] eq 0x77u)) {
            val mac = dataBinary.copyOfRange(2, 8).joinToString(":") { it.toString(16).padStart(2, '0').toUpperCase() }
            val name = String(dataBinary.copyOfRange(56, dataBinary.size).toByteArray())
            val dev = BleDevice(0, mac, name)

            val key = dataBinary.copyOfRange(8, 40).toByteArray()
            val iv  = dataBinary.copyOfRange(40, 56).toByteArray()

            if (KnownDevicesDao.getInstance(this.requireContext().connectionSource).isExist(dev)) {
                this.alert (title = "Add new device", message = "Device with mac: \"$mac\" and name \"$name\" already added.") {
                    okButton {  }
                }.show()
            } else {
                this.alert (
                    title = "Add new device",
                    message = "Device with mac: \"$mac\" and name \"$name\" will be added now.") {
                    okButton {
                        createDev(dev, key, iv)
                    }
                    cancelButton {  }
                }.show()
            }
        } else {
            toast("Bad QR-code, invalid magic tag!").show()
            Log.d("QR DUMP", dataBinary.toByteArray().toHexString())
        }
    }

    private fun createDev(dev : BleDevice, key: ByteArray? = null, iv: ByteArray? = null) {
        activity!!.supportFragmentManager.popBackStackImmediate()
        val pincodeFragment2 = createPincodeCreateFragment(dev, key, iv)
        pincodeFragment2.setTitle("Create your new pincode")
        this@DevicesFragment.activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, pincodeFragment2, "DevicesFragment")
            .addToBackStack("DevicesFragment")
            .commit()
    }
}
