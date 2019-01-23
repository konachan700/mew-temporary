package com.mewhpm.mewsync.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.adapters.RecyclerViewItemActionListener
import com.mewhpm.mewsync.adapters.PairRecyclerViewAdapter
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.database
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.device_disovery_fragment_item_list.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.concurrent.CopyOnWriteArrayList

class DeviceDiscoveryFragment : Fragment(), RecyclerViewItemActionListener<BleDevice> {
    inner class DeviceDiscoveryFragmentBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (!intent.hasExtra(BleService.EXTRA_RESULT_CODE)) return

            when (intent.getIntExtra(BleService.EXTRA_RESULT_CODE, 0)) {
                BleService.EXTRA_RESULT_CODE_OK -> {
                    if ((intent.hasExtra(BleService.EXTRA_DATA_MAC)) && (intent.hasExtra(BleService.EXTRA_DATA_NAME))) {
                        _list.add(BleDevice(0,
                            intent.getStringExtra(BleService.EXTRA_DATA_MAC),
                            intent.getStringExtra(BleService.EXTRA_DATA_NAME)))
                        _adapter.notifyDataSetChanged()
                    }
                }
                BleService.EXTRA_RESULT_CODE_ERROR -> {

                }
                BleService.EXTRA_RESULT_CODE_IN_PROGRESS -> {
                    _colorCounter++
                    if (_colorCounter >= _colorArray.size) _colorCounter = 0

                    view?.waitIcon1?.setImageIcon(
                        Icon.createWithBitmap(
                            IconicsDrawable(context)
                                .icon(GoogleMaterial.Icon.gmd_bluetooth)
                                .sizeDp(40)
                                .color(Color.parseColor(_colorArray[_colorCounter]))
                                .toBitmap()))
                }
            }
        }
    }

    private val _receiver = DeviceDiscoveryFragmentBroadcastReceiver()
    private var _context: Context? = null

    private val _dao = KnownDevicesDao()

    private var _listener: FragmentCloseRequest? = null
    private val _list = CopyOnWriteArrayList<BleDevice>()
    private val _adapter = BleDeviceDiscoveryPairRecyclerViewAdapter()

    private var _colorCounter = 0
    private val _colorArray = arrayOf("#f07070", "#70f070", "#7070f0", "#f070f0", "#70f0f0")

    inner class BleDeviceDiscoveryPairRecyclerViewAdapter: PairRecyclerViewAdapter<BleDevice>(
        mListener = this,
        mType = Fragments.DEVICE_DISCOVERY
    ) {
        override fun requestItem(index: Int): Triple<String, String, GoogleMaterial.Icon> = Triple(
            _list[index].name,
            _list[index].mac,
            if (_dao.isExist(context?.database, _list[index]))
                GoogleMaterial.Icon.gmd_bluetooth_disabled
            else
                GoogleMaterial.Icon.gmd_bluetooth)
        override fun requestCount(): Int = _list.count()
        override fun requestObject(index: Int): BleDevice = _list[index]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _list.clear()

        if (context is FragmentCloseRequest) {
            _listener = context
        }

        val intentFilter = IntentFilter(BleDiscoveryService::class.java.simpleName)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        context.registerReceiver(_receiver, intentFilter)

        _context = context
        val intent = Intent(_context, BleDiscoveryService::class.java)
        intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_START)
        _context?.startService(intent)
    }

    override fun onDetach() {
        super.onDetach()

        if (_context != null) {
            val intent = Intent(_context, BleDiscoveryService::class.java)
            intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_STOP)
            _context?.startService(intent)
            _context?.unregisterReceiver(_receiver)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_disovery_fragment_item_list, container, false)
        _context = this.requireContext()

        view.backLink.setOnClickListener {
            _listener?.close(Fragments.DEVICE_DISCOVERY)
        }

        _adapter.mIconColor = resources.getColor(R.color.colorBrandDark1, _context?.theme)

        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.adapter = _adapter

        view?.waitIcon1?.setImageIcon(
            Icon.createWithBitmap(
                IconicsDrawable(context)
                    .icon(GoogleMaterial.Icon.gmd_bluetooth)
                    .sizeDp(40)
                    .color(resources.getColor(R.color.colorBrandDark1, _context?.theme))
                    .toBitmap()))

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                _listener?.close(Fragments.DEVICE_DISCOVERY)
            }
            true
        }
        return view
    }

    override fun OnClick(dev: BleDevice, source: Fragments) {
        if (_dao.isExist(_context?.database, dev)) {
            alert (title = "Add new device", message = "Device with mac: \"${dev.mac}\" and name \"${dev.name}\" already added.") {
                okButton {  }
            }.show()
        } else {
            alert (title = "Add new device", message = "Device with mac: \"${dev.mac}\" and name \"${dev.name}\" will be added now.") {
                okButton {
                    _dao.addNew(_context?.database, dev)
                    toast("Device added!")
                    _listener?.close(Fragments.DEVICE_DISCOVERY)
                }
                cancelButton {  }
            }.show()
        }
    }

    override fun OnLongClick(dev: BleDevice, source: Fragments) {

    }
}
