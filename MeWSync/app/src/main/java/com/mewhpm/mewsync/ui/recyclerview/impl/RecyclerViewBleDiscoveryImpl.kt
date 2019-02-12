package com.mewhpm.mewsync.ui.recyclerview.impl

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.fragments.PinCodeCreateDialogFragment
import com.mewhpm.mewsync.ui.recyclerview.data.TextPairWithIcon
import com.mewhpm.mewsync.ui.recyclerview.RecyclerViewAbstract
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton

class RecyclerViewBleDiscoveryImpl : RecyclerViewAbstract<BleDevice> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val list = ArrayList<Pair<TextPairWithIcon, BleDevice>>()
    private val pincodeFragment = PinCodeCreateDialogFragment()

    var fragmentManagerRequestEvent : () -> FragmentManager = { throw NotImplementedError("fragmentManagerRequestEvent not set") }
    var deviceExistRequestEvent : (dev : BleDevice) -> Boolean = { throw NotImplementedError("deviceExistRequestEvent not set") }
    var pincodeCreatedEvent : (dev: BleDevice, pincode: String) -> Unit = { _, _ -> throw NotImplementedError("pincodeCreatedEvent not set") }

    private var _currentDevice : BleDevice? = null

    override fun requestList(): ArrayList<Pair<TextPairWithIcon, BleDevice>> = list
    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: BleDevice) {}
    override fun onElementClick(position: Int, item: TextPairWithIcon, obj: BleDevice) {
        if (deviceExistRequestEvent(obj)) {
            context.alert (title = "Add new device", message = "Device with mac: \"${obj.mac}\" and name \"${obj.name}\" already added.") {
                okButton {  }
            }.show()
        } else {
            _currentDevice = obj
            context.alert (title = "Add new device", message = "Device with mac: \"${obj.mac}\" and name \"${obj.name}\" will be added now.") {
                okButton {
                    pincodeFragment.show(fragmentManagerRequestEvent(), "_pincodeCreatorDialog")
                }
                cancelButton {  }
            }.show()
        }
    }

    private fun createDataTextPairWithIcon(dev : BleDevice) : TextPairWithIcon {
        return TextPairWithIcon(
            icon = if (deviceExistRequestEvent(dev)) GoogleMaterial.Icon.gmd_bluetooth_disabled else GoogleMaterial.Icon.gmd_bluetooth,
            iconColor = ContextCompat.getColor(
                context,
                if (deviceExistRequestEvent(dev)) R.color.colorBrandDefaultElement else R.color.colorBrandDark1
            ),
            iconSize = 32,
            text = dev.mac,
            textColor = ContextCompat.getColor(context, R.color.colorBrandDark2),
            title = dev.name,
            titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
        )
    }

    override fun create() {
        super.create()
        pincodeFragment.onPinCodeCreated = { pin -> pincodeCreatedEvent.invoke(_currentDevice!!, pin) }
    }

    fun add(dev : BleDevice) {
        if (list.any { it.second.mac.contentEquals(dev.mac) }) return

        val pair = Pair(createDataTextPairWithIcon(dev), dev)
        list.add(pair)
        this.adapter?.notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        this.adapter?.notifyDataSetChanged()
    }
}