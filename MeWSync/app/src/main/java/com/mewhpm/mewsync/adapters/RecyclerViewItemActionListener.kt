package com.mewhpm.mewsync.adapters

interface RecyclerViewItemActionListener<T> {
    fun onClick(obj: T)
    fun onLongClick(obj: T)
}