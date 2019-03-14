package com.mewhpm.mewsync.ui.recyclerview.data

import com.mikepenz.google_material_typeface_library.GoogleMaterial

data class StaticListItem (
    val title: String,
    val text: String,
    val icon: GoogleMaterial.Icon,
    val onClick: () -> Unit
)