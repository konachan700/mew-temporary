package com.mewhpm.mewsync.adapters

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mewhpm.mewsync.R
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable

import kotlinx.android.synthetic.main.device_disovery_fragment_item.view.*

abstract class PairRecyclerViewAdapter<T>(
    val mListener: RecyclerViewItemActionListener<T>?,
    var mIconSize: Int = 32,
    var mIconColor: Int = 0
) : androidx.recyclerview.widget.RecyclerView.Adapter<PairRecyclerViewAdapter<T>.ViewHolder>() {

    abstract fun requestItem(index: Int): Triple<String, String, GoogleMaterial.Icon>
    abstract fun requestCount(): Int
    abstract fun requestObject(index: Int): T

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_disovery_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val triple = requestItem(position)
        val obj = requestObject(position)

        holder.mContentView.text = triple.second
        holder.mName.text = triple.first

        with(holder.mView) {
            imageView.setImageIcon(
                Icon.createWithBitmap(
                    IconicsDrawable(context)
                        .icon(triple.third)
                        .sizeDp(mIconSize)
                        .color(mIconColor)
                        .toBitmap()
                ))
            tag = obj
            setOnClickListener { v ->
                mListener?.onClick(v.tag as T)
            }
            setOnLongClickListener { v ->
                mListener?.onLongClick(v.tag as T)
                true
            }
        }
    }

    override fun getItemCount(): Int = requestCount()

    inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content
        val mName: TextView = mView.hpmname

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
