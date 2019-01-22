package com.mewhpm.mewsync.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.adapters.PairRecyclerViewAdapter
import com.mewhpm.mewsync.adapters.RecyclerViewItemActionListener
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.database
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import kotlinx.android.synthetic.main.device_disovery_fragment_item_list.*
import kotlinx.android.synthetic.main.device_disovery_fragment_item_list.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.themedAdapterViewFlipper

class KnownDevicesFragment : Fragment(), RecyclerViewItemActionListener<BleDevice> {
    private val _dao = KnownDevicesDao()
    private var _context: Context? = null
    private var _listener: FragmentCloseRequest? = null
    private val _list: ArrayList<BleDevice> = ArrayList()

    inner class BleKnownDevicesPairRecyclerViewAdapter: PairRecyclerViewAdapter<BleDevice>(
        mListener = this,
        mType = Fragments.DEVICE_DISCOVERY
    ) {
        override fun requestItem(index: Int): Triple<String, String, GoogleMaterial.Icon> = Triple(
            _list[index].name,
            _list[index].mac,
            GoogleMaterial.Icon.gmd_bluetooth)
        override fun requestCount(): Int = _list.count()
        override fun requestObject(index: Int): BleDevice = _list[index]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCloseRequest) {
            _listener = context
        }
    }

    private fun refresh(isNotify: Boolean = false) {
        val tempList = _dao.getAll(context?.database)
        _list.clear()
        _list.addAll(tempList)
        if (isNotify) list.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_disovery_fragment_item_list, container, false)
        val adapter = BleKnownDevicesPairRecyclerViewAdapter()

        refresh()
        _context = this.requireContext()

        adapter.mIconColor = resources.getColor(R.color.colorBrandDark1, _context?.theme)

        view.searchBar.visibility = View.GONE
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.adapter = adapter

        return view
    }

    override fun OnClick(dev: BleDevice, source: Fragments) {

    }

    override fun OnLongClick(dev: BleDevice, source: Fragments) {
        alert (title = "Remove device", message = "Do you want delete the device with mac: \"${dev.mac}\" and name \"${dev.name}\"?") {
            okButton {
                _dao.remove(_context?.database, dev)
                refresh(true)
                toast("Device removed!")
                _listener?.close(Fragments.KNOWN_DEVICES)
            }
            cancelButton {  }
        }.show()
    }
}
