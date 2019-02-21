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
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewPasswordsTreeImpl : RecyclerViewAbstract<PassRecord> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    companion object {
        const val ICON_SIZE_DP = 48
        const val ICON_COLOR_RES_ID = R.color.colorBrandDarkGray
    }

    private val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US)

    private val backToParentItem = TextPairWithIcon(
        icon = GoogleMaterial.Icon.gmd_subdirectory_arrow_left,
        iconColor = ContextCompat.getColor(context, R.color.colorBrandBlack),
        iconSize = ICON_SIZE_DP,
        text = "Back to parent element",
        textColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
        title = "...",
        titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
    )
    private val passRecordForBackItem = PassRecord(PassRecord.TYPE_GO_TO_PARENT)
    private val list = ArrayList<Pair<TextPairWithIcon, PassRecord>>()

    var onDeleteEvent : (record : PassRecord) -> Unit = {}
    var onEditEvent : (record : PassRecord) -> Unit = {}

    var onItemClickEvent : (record : PassRecord) -> Unit = {}
    var onBackEvent : () -> Unit = {}

    fun addItems(items: List<PassRecord>, isRoot: Boolean = false) {
        list.clear()
        val endList : List<Pair<TextPairWithIcon, PassRecord>> = items.map {
            when (it.nodeType) {
                PassRecord.TYPE_FOLDER -> {
                    Pair(
                        TextPairWithIcon(
                            icon = GoogleMaterial.Icon.gmd_folder,
                            iconColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
                            iconSize = ICON_SIZE_DP,
                            text = if (it.text.isBlank()) "Created " + formatter.format(Date(it.timestamp)) else it.text,
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
                            text = "Created " + formatter.format(Date(it.timestamp)),
                            textColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
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
                            text = "Data corrupted (id=${it.id})",
                            textColor = ContextCompat.getColor(context, ICON_COLOR_RES_ID),
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

    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: PassRecord) {
        if (obj.nodeType == PassRecord.TYPE_GO_TO_PARENT) return

        val actions = listOf("Edit", "Delete")
        context.selector("Actions", actions) { _, index ->
            when (index) {
                0 -> { onEditEvent.invoke(obj) }
                1 -> { onDeleteEvent.invoke(obj) }
            }
        }
    }
}