package com.mewhpm.mewsync.ui.recyclerview.impl

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.fragments.PinCodeVerifyDialogFragment
import com.mewhpm.mewsync.ui.recyclerview.data.TextPairWithIcon
import com.mewhpm.mewsync.ui.recyclerview.RecyclerViewAbstract
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.selector

class RecyclerViewDevicesImpl : RecyclerViewAbstract<BleDevice> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val list = ArrayList<Pair<TextPairWithIcon, BleDevice>>()
    private val pincodeFragment = PinCodeVerifyDialogFragment()

    var fragmentManagerRequestEvent : () -> FragmentManager = { throw NotImplementedError("fragmentManager not set") }
    var pinCodeEnteredEvent : (pincode: String, obj: BleDevice) -> Boolean = { _, _ -> throw NotImplementedError("pinCodeEnteredEvent not set") }
    var deleteEvent : (position: Int, item: TextPairWithIcon, obj: BleDevice) -> Unit = { _, _, _ -> throw NotImplementedError("deleteEvent not set") }
    var setDefaultEvent : (position: Int, item: TextPairWithIcon, obj: BleDevice) -> Unit = { _, _, _ -> throw NotImplementedError("setDefaultEvent not set") }

    private var _currentDevice : BleDevice? = null

    override fun requestList(): ArrayList<Pair<TextPairWithIcon, BleDevice>> = list
    override fun onElementClick(position: Int, item: TextPairWithIcon, obj: BleDevice) {
        _currentDevice = obj
        pincodeFragment.show(fragmentManagerRequestEvent.invoke(), "pincode_dialog")
    }

    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: BleDevice) {
        val actions = listOf("Set default", "Delete")
        context.selector("Actions", actions) { _, index ->
            when (index) {
                0 -> sefDefault(position, item, obj)
                1 -> {
                    context.alert (title = "Remove device", message = "Do you want delete the device with mac: \"${obj.mac}\" and name \"${obj.name}\"?") {
                        okButton {
                            remove(position, item, obj)
                        }
                        cancelButton {  }
                    }.show()
                }
            }
        }
    }

    override fun create() {
        super.create()
        pincodeFragment.onPincodeEntered = { pin -> pinCodeEnteredEvent.invoke(pin, _currentDevice!!) }
    }

    private fun createDataTextPairWithIcon(dev : BleDevice) : TextPairWithIcon {
        return TextPairWithIcon(
            icon = GoogleMaterial.Icon.gmd_bluetooth,
            iconColor = ContextCompat.getColor(context, R.color.colorBrandDark1),
            iconSize = 32,
            text = dev.mac,
            textColor = ContextCompat.getColor(context, R.color.colorBrandDark2),
            title = dev.name,
            titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
        )
    }

    fun add(dev : BleDevice) {
        val pair = Pair(createDataTextPairWithIcon(dev), dev)
        list.add(pair)
        this.adapter?.notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        this.adapter?.notifyDataSetChanged()
    }

    private fun sefDefault(position: Int, item: TextPairWithIcon, obj: BleDevice) {
        list.forEach { it.first.iconColor = ContextCompat.getColor(context, R.color.colorBrandDark1) }
        item.iconColor = ContextCompat.getColor(context, R.color.colorBrandDefaultElement)
        this.adapter?.notifyDataSetChanged()
        setDefaultEvent.invoke(position, item, obj)
    }

    private fun remove(position: Int, item: TextPairWithIcon, obj: BleDevice) {
        list.removeAt(position)
        this.adapter?.notifyDataSetChanged()
        deleteEvent(position, item, obj)
    }

    fun reload() {
        this.adapter?.notifyDataSetChanged()
    }
}