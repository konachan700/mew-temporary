package com.mewhpm.mewsync.fragments

import android.content.Context
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
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.concurrent.CopyOnWriteArrayList

class DeviceDiscoveryFragment : Fragment(), RecyclerViewItemActionListener<BleDevice>, BleDiscoveryEvents {
    private val _searcher: BleDeviceSearch = BleDeviceSearchImpl()
    private val _dao = KnownDevicesDao()
    private var _context: Context? = null
    private var _listener: FragmentCloseRequest? = null
    private val _list = CopyOnWriteArrayList<BleDevice>()
    private val _adapter = BleDeviceDiscoveryPairRecyclerViewAdapter()

    override fun onDeviceFound() {
        _adapter.notifyDataSetChanged()
    }

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
        _searcher.bleDiscoverStart(context, _list, this)
        if (context is FragmentCloseRequest) {
            _listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        _searcher.bleDiscoverStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_disovery_fragment_item_list, container, false)
        if (view is androidx.recyclerview.widget.RecyclerView) {
            with(view) {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                adapter = _adapter
            }
        }

//        if (_searcher is BleDeviceSearchDummyImpl) {
//            _list.clear()
//            _list.addAll(_searcher.bleDiscover())
//        }

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                _listener?.close(Fragments.DEVICE_DISCOVERY)
            }
            true
        }
        _context = this.requireContext()
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
