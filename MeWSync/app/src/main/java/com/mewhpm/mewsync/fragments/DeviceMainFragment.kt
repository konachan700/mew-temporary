package com.mewhpm.mewsync.fragments

import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mewhpm.mewsync.R
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.device_fragment.view.*

class DeviceMainFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.device_fragment, container, false)
        //view.bottomView.setOnNavigationItemSelectedListener(this)



        return view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.mContacts -> {
//
//            }
//            R.id.mPasswords -> {
//
//            }
//            R.id.mSettings -> {
//
//            }
//        }

        return true
    }
}