package com.mewhpm.mewsync.ui.recyclerview.impl

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.ui.recyclerview.RecyclerViewAbstract
import com.mewhpm.mewsync.ui.recyclerview.adapters.SimpleTextIconAdapter
import com.mewhpm.mewsync.ui.recyclerview.data.TextPairWithIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import org.jetbrains.anko.selector
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewForPasswordsKeyboard : RecyclerViewAbstract<PassRecord> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    companion object {
        const val ICON_SIZE_DP = 24
        const val ICON_COLOR_RES_ID = R.color.colorBrandDarkGray
    }

    private val backToParentItem = TextPairWithIcon(
        icon = GoogleMaterial.Icon.gmd_subdirectory_arrow_left,
        iconColor = ContextCompat.getColor(context, R.color.colorBrandBlack),
        iconSize = ICON_SIZE_DP,
        text = "",
        textColor = 0,
        title = "...",
        titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
    )
    private val passRecordForBackItem = PassRecord(PassRecord.TYPE_GO_TO_PARENT)
    private val list = ArrayList<Pair<TextPairWithIcon, PassRecord>>()

    var onItemClickEvent : (record : PassRecord) -> Unit = {}
    var onBackEvent : () -> Unit = {}

    fun addItems(items: List<PassRecord>, isRoot: Boolean = false) {
        list.clear()
        (this.adapter as SimpleTextIconAdapter)._index = -1
        val endList : List<Pair<TextPairWithIcon, PassRecord>> = items.map {
            when (it.nodeType) {
                PassRecord.TYPE_FOLDER -> {
                    Pair(
                        TextPairWithIcon(
                            icon = GoogleMaterial.Icon.gmd_folder,
                            iconColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
                            iconSize = ICON_SIZE_DP,
                            text = it.text,
                            textColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
                            title = it.title,
                            titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
                        ), it
                    )
                }
                PassRecord.TYPE_RECORD -> {
                    Pair(
                        TextPairWithIcon(
                            icon = GoogleMaterial.Icon.gmd_vpn_key,
                            iconColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
                            iconSize = ICON_SIZE_DP,
                            text = "",
                            textColor = 0,
                            title = it.title,
                            titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
                        ), it
                    )
                }
                else -> {
                    Pair(
                        TextPairWithIcon(
                            icon = GoogleMaterial.Icon.gmd_sentiment_dissatisfied,
                            iconColor = ContextCompat.getColor(context, R.color.colorBrandDefaultElement),
                            iconSize = ICON_SIZE_DP,
                            text = "",
                            textColor = 0,
                            title = "Broken element",
                            titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
                        ), it
                    )
                }
            }
        }

        if (!isRoot) list.add(Pair(backToParentItem, passRecordForBackItem))
        list.addAll(endList)
        this.adapter?.notifyDataSetChanged()
    }

    override fun requestList(): ArrayList<Pair<TextPairWithIcon, PassRecord>> = list
    override fun onElementClick(position: Int, item: TextPairWithIcon, obj: PassRecord) {
        when (obj.nodeType) {
            PassRecord.TYPE_GO_TO_PARENT -> onBackEvent.invoke()
            PassRecord.TYPE_FOLDER -> onItemClickEvent.invoke(obj)
            PassRecord.TYPE_RECORD -> onItemClickEvent.invoke(obj)
        }
    }

    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: PassRecord) { }

    override fun create() {
        super.create()
        this.adapter = SimpleTextIconAdapterImpl()
    }
}