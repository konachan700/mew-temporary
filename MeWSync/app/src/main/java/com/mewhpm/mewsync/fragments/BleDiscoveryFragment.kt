package com.mewhpm.mewsync.fragments
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.BleDiscoveryService
import com.mewhpm.mewsync.services.BleService
import com.mewhpm.mewsync.ui.recyclerview.impl.RecyclerViewBleDiscoveryImpl
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.x01_ble_disovery_fragment.view.*

class BleDiscoveryFragment : Fragment() {
    var onExitEvent : () -> Unit = {}
    var onDeviceSelectEvent : (ble: BleDevice) -> Unit = {}

    private val _receiver = DeviceDiscoveryFragmentBroadcastReceiver()

    private var _rvList: RecyclerViewBleDiscoveryImpl? = null
    private var _view: View? = null

    private var colorCounter = 0
    private var colorArray = arrayOf("#FF9999", "#99FF99", "#FFFF99")
    inner class DeviceDiscoveryFragmentBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (!intent.hasExtra(BleService.EXTRA_RESULT_CODE)) return

            when (intent.getIntExtra(BleService.EXTRA_RESULT_CODE, 0)) {
                BleService.EXTRA_RESULT_CODE_OK -> {
                    if ((intent.hasExtra(BleService.EXTRA_DATA_MAC)) && (intent.hasExtra(BleService.EXTRA_DATA_NAME))) {
                        _rvList?.add(
                            BleDevice(0,
                                intent.getStringExtra(BleService.EXTRA_DATA_MAC),
                                intent.getStringExtra(BleService.EXTRA_DATA_NAME))
                        )
                    }
                }
                BleService.EXTRA_RESULT_CODE_ERROR -> { onExitEvent.invoke() }
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

    override fun onPause() {
        super.onPause()

        val intent = Intent(context, BleDiscoveryService::class.java)
        intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_STOP)
        context?.startService(intent)
        context?.unregisterReceiver(_receiver)
        onExitEvent.invoke()
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(BleDiscoveryService::class.java.simpleName)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        context?.registerReceiver(_receiver, intentFilter)

        val intent = Intent(context, BleDiscoveryService::class.java)
        intent.putExtra(BleService.EXTRA_ACTION, BleDiscoveryService.BLE_DISCOVERY_START)
        context?.startService(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.x01_ble_disovery_fragment, container, false)
        _rvList = _view!!.discoveryList

        with (_view!!) {
            button.setCompoundDrawables(IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_bluetooth).sizeDp(32).color(Color.WHITE),null,null,null)
            button.setOnClickListener {
                onExitEvent.invoke()
            }
        }

        with (_rvList!!) {
            create()
            deviceExistRequestEvent = { dev ->
                KnownDevicesDao.getInstance(this@BleDiscoveryFragment.requireContext().connectionSource).isExist(dev) }
            deviceItemClickEvent = { dev -> onDeviceSelectEvent.invoke(dev) }
        }
        return _view
    }
}