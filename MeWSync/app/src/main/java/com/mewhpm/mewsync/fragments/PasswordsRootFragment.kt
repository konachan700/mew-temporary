package com.mewhpm.mewsync.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.ui.fragmentpages.FragmentPage
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil


class PasswordsRootFragment : FragmentPage() {
    private var _view: View? = null

    var onFragmentAppear : (fragment: Fragment, menuPresent: Boolean) -> Unit = { _, _ -> }

    override fun onResume() {
        super.onResume()
        onFragmentAppear.invoke(this, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.setHasOptionsMenu(true)
        _view = inflater.inflate(R.layout.x02_fragment_passwords, container, false)
        return _view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        IconicsMenuInflaterUtil.inflate(inflater, this.requireContext(), R.menu.password_root_fragment_menu, menu)
        val mItem: android.widget.SearchView = menu.findItem(R.id.passwords_fragment_search).actionView as android.widget.SearchView
        mItem.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean = false
            override fun onQueryTextSubmit(query: String): Boolean {

                mItem.clearFocus()
                return true
            }
        })
    }
}