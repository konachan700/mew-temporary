package com.mewhpm.mewsync.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.adapters.PairRecyclerViewAdapter
import com.mewhpm.mewsync.adapters.RecyclerViewItemActionListener
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.database
import com.mewhpm.mewsync.data.BleDevice
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.device_disovery_fragment_item_list.*
import kotlinx.android.synthetic.main.device_disovery_fragment_item_list.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

class DevicesFragment : Fragment(), RecyclerViewItemActionListener<BleDevice> {
    private val ACCESS_COARSE_LOCATION = 43
    private val _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _searchDialog = DeviceDiscoveryFragment()

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

    inner class BleKnownDevicesPairRecyclerViewAdapter: PairRecyclerViewAdapter<BleDevice>(
        mListener = this, mType = Fragments.DEVICE_DISCOVERY
    ) {
        override fun requestItem(index: Int): Triple<String, String, GoogleMaterial.Icon> = Triple(
            _list[index].name,
            _list[index].mac,
            GoogleMaterial.Icon.gmd_bluetooth)
        override fun requestCount(): Int = _list.count()
        override fun requestObject(index: Int): BleDevice = _list[index]
    }

    private var _context: Context? = null
    private val _adapter = BleKnownDevicesPairRecyclerViewAdapter()
    private val _dao = KnownDevicesDao()

    private fun refresh(isNotify: Boolean = false) {
        _list.clear()
        _list.addAll(_dao.getAll(_context?.database))
        if (isNotify) list.adapter?.notifyDataSetChanged()
    }

    private val _list: ArrayList<BleDevice> = ArrayList()

    override fun onAttach(context: Context) {
        _context = context
        super.onAttach(context)
    }

    override fun onDetach() {
        _context = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_disovery_fragment_item_list, container, false)
        view.fab.setImageIcon(Icon.createWithBitmap(
            IconicsDrawable(_context).icon(GoogleMaterial.Icon.gmd_bluetooth_searching).sizeDp(32).color(Color.WHITE).toBitmap()
        ))
        view.fab.setOnClickListener {
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN), ACCESS_COARSE_LOCATION)
        }

        refresh()
        _searchDialog.closeListener = {
            refresh(true)
        }

        _adapter.mIconColor = resources.getColor(R.color.colorBrandDark1, _context?.theme)
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(_context)
        view.list.adapter = _adapter

        return view
    }

    override fun OnClick(dev: BleDevice, source: Fragments) {

    }

    override fun OnLongClick(dev: BleDevice, source: Fragments) {
        deleteDevice(dev)
    }

    private fun deleteDevice(dev: BleDevice) {
        alert (title = "Remove device", message = "Do you want delete the device with mac: \"${dev.mac}\" and name \"${dev.name}\"?") {
            okButton {
                _dao.remove(_context?.database, dev)
                refresh(true)
                toast("Device removed!")
            }
            cancelButton {  }
        }.show()
    }
}
