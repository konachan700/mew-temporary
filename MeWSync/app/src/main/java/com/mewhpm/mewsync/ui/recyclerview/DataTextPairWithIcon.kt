package com.mewhpm.mewsync.ui.recyclerview

import com.mikepenz.google_material_typeface_library.GoogleMaterial

data class DataTextPairWithIcon (
    var icon: GoogleMaterial.Icon,
    var iconColor: Int,
    var iconSize: Int,
    var title: String,
    var text: String,
    var titleColor: Int,
    var textColor: Int
)