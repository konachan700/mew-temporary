package com.mewhpm.mewsync.ui.recyclerview.impl

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.ui.recyclerview.RecyclerViewAbstract
import com.mewhpm.mewsync.ui.recyclerview.data.StaticListItem
import com.mewhpm.mewsync.ui.recyclerview.data.TextPairWithIcon

class RecyclerViewStaticList : RecyclerViewAbstract<StaticListItem> {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val ICON_SIZE_DP = 40
    }

    private val list = ArrayList<Pair<TextPairWithIcon, StaticListItem>>()

    override fun requestList(): java.util.ArrayList<Pair<TextPairWithIcon, StaticListItem>> = list
    override fun onElementLongClick(position: Int, item: TextPairWithIcon, obj: StaticListItem) { }
    override fun onElementClick(position: Int, item: TextPairWithIcon, obj: StaticListItem) = obj.onClick.invoke()

    override fun create() {
        super.create()
        this.adapter = TextPairWithIconAdapterImpl()
    }

    fun addItems(vararg items: StaticListItem) {
        items.forEach {
            val item = TextPairWithIcon(
                icon = it.icon,
                iconColor = ContextCompat.getColor(context, R.color.colorBrandBlack),
                iconSize = ICON_SIZE_DP,
                text = it.text,
                textColor = ContextCompat.getColor(context, R.color.colorBrandBlack),
                title = it.title,
                titleColor = ContextCompat.getColor(context, R.color.colorBrandBlack)
            )
            list.add(Pair(item, it))
        }
        this.adapter?.notifyDataSetChanged()
    }
}