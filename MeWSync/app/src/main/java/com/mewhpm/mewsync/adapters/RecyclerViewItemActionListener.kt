package com.mewhpm.mewsync.adapters

import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.fragments.Fragments

interface RecyclerViewItemActionListener<T> {
    fun OnClick(obj: T, source: Fragments)
    fun OnLongClick(obj: T, source: Fragments)
}