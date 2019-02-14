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
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.dao.database
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.ui.pinpad.verifyPin
import com.mewhpm.mewsync.ui.recyclerview.impl.RecyclerViewDevicesImpl
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.x01_known_devices_list.view.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class DevicesFragment : Fragment() {
    companion object {
        private const val ACCESS_COARSE_LOCATION = 43
    }

    private val _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _searchDialog = DeviceDiscoveryDialogFragment()

    private var _pref: SharedPreferences? = null
    private var _rvDevices : RecyclerViewDevicesImpl? = null
    private var _view: View? = null
    private var dao : KnownDevicesDao? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!_bluetoothAdapter.isEnabled) {
            alert("Bluetooth", "Bluetooth is disabled!") {
                okButton {  }
            }.show()
        } else {
            when (requestCode) {
                ACCESS_COARSE_LOCATION -> {
                    if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                        _searchDialog.show(fragmentManager!!, "_searchDialog")
                    } else {
                        alert("No permissions to BLE", "Bluetooth") {
                            okButton {  }
                        }
                    }
                }
            }
        }
    }

    private fun refreshFromDb() {
        val list: List<BleDevice> = getDao().getAll()
        if (_view != null) {
            _rvDevices!!.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            _view!!.noItemsInList1.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        _rvDevices?.clear()
        list.forEach { _rvDevices?.add(it) }
    }

    private fun openDevice(enteredPincode: String, device: BleDevice) {
        val intent = Intent(this.context, DeviceActivity::class.java)
        intent.putExtra("pincode", enteredPincode)
        intent.putExtra("dev_mac", device.mac)
        intent.putExtra("dev_name", device.name)
        startActivityForResult(intent, 1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _pref = PreferenceManager.getDefaultSharedPreferences(context)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.x01_known_devices_list, container, false)

        _rvDevices = _view!!.listRV1
        with (_rvDevices!!) {
            create()
            deleteEvent = { _, _, dev ->
                getDao().remove(dev)
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
                verifyPin { pin ->
                    val retVal = CryptoUtils.verifyPinCode(_pref, pin, dev)
                    if (retVal) openDevice(pin, dev)
                    retVal
                }
            }
        }

        with (_view!!.addNewBleDevBtn1) {
            setImageIcon(Icon.createWithBitmap(
                IconicsDrawable(requireContext())
                    .icon(GoogleMaterial.Icon.gmd_bluetooth_searching).sizeDp(32).color(Color.WHITE).toBitmap()
            ))
            setOnClickListener {
                requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN), ACCESS_COARSE_LOCATION)
            }
        }

        _searchDialog.closeListener = {
            refreshFromDb()
        }

        refreshFromDb()

        return _view
    }
}
