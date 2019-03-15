package com.mewhpm.mewsync.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.DeviceActivity
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.services.BleService
import com.mewhpm.mewsync.ui.recyclerview.data.StaticListItem
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import kotlinx.android.synthetic.main.x03_sync_with_mew.view.*
import java.io.Serializable

class DeviceOverviewFragment : androidx.fragment.app.Fragment() {
    inner class DeviceOverviewFragmentBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }
    private var _view: View? = null
    private val _receiver = DeviceOverviewFragmentBroadcastReceiver()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.setHasOptionsMenu(false)
        _view = inflater.inflate(R.layout.x03_sync_with_mew, container, false)
        _view!!.listOfStaticElements1.create()
        _view!!.listOfStaticElements1.addItems(
            StaticListItem("MeW keyboard", "Emulate real usb keyboard", GoogleMaterial.Icon.gmd_keyboard) {
                if (KnownDevicesDao.isDeviceBroken(DeviceActivity.currentDeviceMac)) return@StaticListItem
                sendIntent(
                    Pair(BleService.EXTRA_ACTION, BleService.EXTRA_ACTION_SEND_CMD),
                    Pair(BleService.EXTRA_DATA_CMD, 0x4300),
                    Pair(BleService.EXTRA_DATA_MAC, DeviceActivity.currentDeviceMac),
                    Pair(BleService.EXTRA_DATA_DATA, Base64.encodeToString(byteArrayOf(0x00, 0x00), Base64.DEFAULT))
                )
            },
            StaticListItem("Sync passwords", "Sync passwords with MeW", GoogleMaterial.Icon.gmd_sync) {

            }
        )
        return _view
    }

    private fun sendIntent(vararg pairs: Pair<String, Serializable>) {
        val intent = Intent(context, BleService::class.java)
        pairs.forEach { intent.putExtra(it.first, it.second) }
        context?.startService(intent)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(_receiver, IntentFilter(BleService::class.java.simpleName).also { it.addCategory(Intent.CATEGORY_DEFAULT) })



    }

    override fun onPause() {
        super.onPause()


        context?.unregisterReceiver(_receiver)
    }
}