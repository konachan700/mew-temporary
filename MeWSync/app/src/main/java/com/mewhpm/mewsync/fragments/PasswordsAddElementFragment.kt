package com.mewhpm.mewsync.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.ui.fragmentpages.FragmentPage
import kotlinx.android.synthetic.main.x02_fragment_add_directory.view.*
import kotlinx.android.synthetic.main.x02_fragment_add_password.view.*
import java.lang.IllegalArgumentException

class PasswordsAddElementFragment : FragmentPage() {
    companion object {
        const val TYPE_ADD_DIR = 10L
        const val TYPE_ADD_PASSWORD = 20L
    }

    var type = TYPE_ADD_DIR

    private var _view: View? = null
    private var _currentId = 0
    private var _parentId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.setHasOptionsMenu(true)
        val result = activity!!.intent.extras!!

        _currentId = result.getInt("elementId", 0)
        _parentId = result.getInt("parentId", 0)

        when (type) {
            TYPE_ADD_DIR -> {
                _view = inflater.inflate(R.layout.x02_fragment_add_directory, container, false)
                _view!!.editTextDirectoryName.setText(result.getString("editTextDirectoryName", ""))
                _view!!.editTextDirectoryDescription.setText(result.getString("editTextDirectoryDescription", ""))

            }
            TYPE_ADD_PASSWORD -> {
                _view = inflater.inflate(R.layout.x02_fragment_add_password, container, false)
                _view!!.editTextPassURL.setText(result.getString("editTextPassURL", ""))
                _view!!.editTextPassLogin.setText(result.getString("editTextPassLogin", ""))
                _view!!.editTextPassDescription.setText(result.getString("editTextPassDescription", ""))
            }
            else -> throw IllegalArgumentException("PasswordsAddElementFragment::onCreateView - Strange error")
        }

        return _view
    }
}