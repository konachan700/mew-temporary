package com.mewhpm.mewsync.services

interface BleDiscoveryEvents {
    fun onDeviceFound()
    fun onSearching()
}