package com.mewhpm.mewsync.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.R
import kotlinx.android.synthetic.main.x02_fragment_passwords.view.*

class PasswordsRootFragment : Fragment() {
    private var _view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.x02_fragment_passwords, container, false)
        with (_view!!.addNewPasswordElementBtn1) {
            setImageIcon(
                android.graphics.drawable.Icon.createWithBitmap(
                    com.mikepenz.iconics.IconicsDrawable(requireContext())
                        .icon(com.mikepenz.google_material_typeface_library.GoogleMaterial.Icon.gmd_add_circle_outline).sizeDp(32).color(
                            android.graphics.Color.WHITE
                        ).toBitmap()
                )
            )
            setOnClickListener {

            }
        }
        return _view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }
}