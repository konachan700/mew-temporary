package com.mewhpm.mewsync.ui.recyclerview.impl

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.ui.recyclerview.RecyclerViewAbstract
import com.mewhpm.mewsync.ui.recyclerview.data.TextPairWithIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.selector

class RecyclerViewPasswordsTreeImpl : RecyclerViewAbstract<PassRecord> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val backToParentItem = TextPairWithIcon(
        icon = GoogleMaterial.Icon.gmd_bluetooth,
        iconColor = ContextCompat.getColor(context, R.color.colorBrandDark1),
        iconSize = 32,
        text = "Back to parent element",
        textColor = ContextCompat.getColor(context, R.color.colorBrandDark2),
        title = "...",
        titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
    )
    private val passRecordForBackItem = PassRecord(PassRecord.TYPE_GO_TO_PARENT)
    private val list = ArrayList<Pair<TextPairWithIcon, PassRecord>>()

    fun addItems(items: ArrayList<Pair<TextPairWithIcon, PassRecord>>, isRoot: Boolean = false) {
        list.clear()
        if (!isRoot) list.add(Pair(backToParentItem, passRecordForBackItem))
        list.addAll(items)
        this.adapter?.notifyDataSetChanged()
    }

    override fun requestList(): ArrayList<Pair<TextPairWithIcon, PassRecord>> = list
    override fun onElementClick(position: Int, item: TextPairWithIcon, obj: PassRecord) {

    }

    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: PassRecord) {
        if (obj.nodeType == PassRecord.TYPE_GO_TO_PARENT) return

        val actions = listOf("Edit", "Delete")
        context.selector("Actions", actions) { _, index ->
            when (index) {
                0 -> { }
                1 -> { }
            }
        }
    }
}