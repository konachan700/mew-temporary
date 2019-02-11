package com.mewhpm.mewsync.ui.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewAbstract<T> : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract fun requestList() : List<Pair<DataTextPairWithIcon, T>>
    abstract fun onElementClick(position: Int, item: DataTextPairWithIcon, obj: T)
    abstract fun onElementLongClick(position: Int, item: DataTextPairWithIcon, obj: T)

    inner class AdapterTextPairWithIconImpl : AdapterTextPairWithIcon() {
        override fun requestDataTextPairWithIcon(position: Int): DataTextPairWithIcon = requestList()[position].first
        override fun requestListSize(): Int = requestList().size
        override fun requestContext(): Context = this@RecyclerViewAbstract.context

        override fun onElementClick(position: Int) {
            onElementClick(position, requestList()[position].first, requestList()[position].second)
        }

        override fun onElementLongClick(position: Int) {
            onElementLongClick(position, requestList()[position].first, requestList()[position].second)
        }
    }

    open fun create() {
        this.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)
        this.adapter = AdapterTextPairWithIconImpl()
    }
}