package com.mewhpm.mewsync.fragments

import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.adapters.PairRecyclerViewAdapter
import com.mewhpm.mewsync.adapters.RecyclerViewItemActionListener
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.database
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.BleDiscoveryService
import com.mewhpm.mewsync.services.BleService
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.mew_disovery_fragment_popup.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.util.concurrent.CopyOnWriteArrayList


class DeviceDiscoveryDialogFragment : DialogFragment() , RecyclerViewItemActionListener<BleDevice> {
    var closeListener: () -> Unit = {}

    private var colorCounter = 0
    private var colorArray = arrayOf("#FF9999", "#99FF99", "#FFFF99")
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
                    this@DeviceDiscoveryDialogFragment.dismiss()
                }
                BleService.EXTRA_RESULT_CODE_IN_PROGRESS -> {
                    if (context == null) return
                    colorCounter++
                    if (colorCounter >= colorArray.size) colorCounter = 0
                    view?.button?.setCompoundDrawables(
                        IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_bluetooth).sizeDp(32)
                            .color(Color.parseColor(colorArray[colorCounter])), null, null, null)
                }
            }
        }
    }

    private val _receiver = DeviceDiscoveryFragmentBroadcastReceiver()
    private val _dao = KnownDevicesDao()
    private val _list = CopyOnWriteArrayList<BleDevice>()
    private val _adapter = BleDeviceDiscoveryPairRecyclerViewAdapter()

    inner class BleDeviceDiscoveryPairRecyclerViewAdapter: PairRecyclerViewAdapter<BleDevice>(
        mListener = this, mType = Fragments.DEVICE_DISCOVERY
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val intent = Intent(context, BleDiscoveryService::class.java)
        intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_STOP)
        context?.startService(intent)
        context?.unregisterReceiver(_receiver)
        closeListener.invoke()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _list.clear()

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Searching...")

        val intentFilter = IntentFilter(BleDiscoveryService::class.java.simpleName)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        context?.registerReceiver(_receiver, intentFilter)

        val intent = Intent(context, BleDiscoveryService::class.java)
        intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_START)
        context?.startService(intent)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.mew_disovery_fragment_popup, container, false)

        _adapter.mIconColor = resources.getColor(R.color.colorBrandDark1, context?.theme)
        view.discoveryList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.discoveryList.adapter = _adapter
        view.button.setCompoundDrawables(IconicsDrawable(context)
            .icon(GoogleMaterial.Icon.gmd_bluetooth).sizeDp(32).color(Color.WHITE),null,null,null)
        view.button.setOnClickListener {
            this@DeviceDiscoveryDialogFragment.dismiss()
        }

        return view
    }

    override fun OnClick(dev: BleDevice, source: Fragments) {
        if (_dao.isExist(context?.database, dev)) {
            alert (title = "Add new device", message = "Device with mac: \"${dev.mac}\" and name \"${dev.name}\" already added.") {
                okButton {  }
            }.show()
        } else {
            alert (title = "Add new device", message = "Device with mac: \"${dev.mac}\" and name \"${dev.name}\" will be added now.") {
                okButton {
                    _dao.addNew(context?.database, dev)
                    toast("Device added!")
                    this@DeviceDiscoveryDialogFragment.dismiss()
                }
                cancelButton {  }
            }.show()
        }
    }

    override fun OnLongClick(dev: BleDevice, source: Fragments) { }
}
