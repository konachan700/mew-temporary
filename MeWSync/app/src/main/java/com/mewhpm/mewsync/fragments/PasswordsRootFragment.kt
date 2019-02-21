package com.mewhpm.mewsync.fragments

import android.os.Bundle
import android.view.*
import com.google.gson.Gson
import com.mewhpm.mewsync.DeviceActivity
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.dao.PasswordsDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.data.PassRecordMetadata
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_DIR_DESC
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_DIR_NAME
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_ELEMENT_ID
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_PARENT_ID
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_PASS_DESC
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_PASS_LOGIN
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_PASS_URL
import com.mewhpm.mewsync.fragments.PasswordsAddElementFragment.Companion.KEY_TYPE
import com.mewhpm.mewsync.utils.fixColorOfSearchBar
import com.mewhpm.mewsync.utils.hideKeyboard
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.x02_fragment_passwords.view.*
import java.util.*

class PasswordsRootFragment : androidx.fragment.app.Fragment() {
    private var _view: View? = null
    private var _currentFolderId = 0L

    private val gson = Gson()

    private val onOkClick : (bundle: Bundle) -> Unit = { bundle ->
        val id = bundle.getLong(KEY_ELEMENT_ID)
        val dao = PasswordsDao.getInstance(this.requireContext().connectionSource)
        when (bundle.getLong(KEY_TYPE, 0)) {
            PassRecord.TYPE_FOLDER -> {
                if (id == 0L) {
                    val entity = createDirectoryRecord(bundle)
                    dao.create(entity)
                } else {
                    val element = dao.getById(id)
                    if (element != null) {
                        element.text = bundle.getString(KEY_DIR_DESC, "error")
                        element.title = bundle.getString(KEY_DIR_NAME, "error")
                        dao.save(element)
                    }
                }
            }
            PassRecord.TYPE_RECORD -> {
                if (id == 0L) {
                    val entity = createPasswordRecord(bundle)
                    dao.create(entity)
                } else {
                    val element = dao.getById(id)
                    if (element != null) {
                        val meta = PassRecordMetadata()
                        meta.url = bundle.getString(KEY_PASS_URL, "error")
                        meta.login = bundle.getString(KEY_PASS_LOGIN, "error")

                        element.text = ""
                        element.title = bundle.getString(KEY_PASS_DESC, "error")
                        element.metadataJson = gson.toJson(meta)
                        dao.save(element)
                    }
                }
            }
        }
    }

    private fun createDirectoryRecord(bundle: Bundle) : PassRecord {
        val entity = PassRecord()
        entity.nodeType = PassRecord.TYPE_FOLDER
        entity.text = bundle.getString(KEY_DIR_DESC, "error")
        entity.title = bundle.getString(KEY_DIR_NAME, "error")
        entity.hwUID = 0L
        entity.timestamp = Date().time
        entity.parentId = bundle.getLong(KEY_PARENT_ID, 0)
        entity.deviceAddr = DeviceActivity.currentDeviceMac
        return entity
    }

    private fun createPasswordRecord(bundle: Bundle) : PassRecord {
        val entity = PassRecord()
        entity.nodeType = PassRecord.TYPE_RECORD
        entity.text = ""
        entity.title = bundle.getString(KEY_PASS_DESC, "error")
        entity.hwUID = 0L
        entity.timestamp = Date().time
        entity.parentId = bundle.getLong(KEY_PARENT_ID, 0)
        entity.deviceAddr = DeviceActivity.currentDeviceMac

        val meta = PassRecordMetadata()
        meta.url = bundle.getString(KEY_PASS_URL, "error")
        meta.login = bundle.getString(KEY_PASS_LOGIN, "error")

        entity.metadataJson = gson.toJson(meta)
        return entity
    }

    private fun refresh() {
        val dao = PasswordsDao.getInstance(this.requireContext().connectionSource)
        val list = dao.getAllChild(_currentFolderId)
        if (_view != null) {
            _view!!.listRVPasswords1.visibility = if (list.isEmpty() && _currentFolderId == 0L) View.GONE else View.VISIBLE
            _view!!.noItemsInList2.visibility = if (list.isEmpty() && _currentFolderId == 0L) View.VISIBLE else View.GONE
        }

        _view!!.listRVPasswords1.addItems(list, _currentFolderId == 0L)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.setHasOptionsMenu(true)
        _view = inflater.inflate(R.layout.x02_fragment_passwords, container, false)
        with (_view!!.listRVPasswords1) {
            create()
            onBackEvent = {
                val dao = PasswordsDao.getInstance(this@PasswordsRootFragment.requireContext().connectionSource)
                val parent = dao.getParent(_currentFolderId)
                _currentFolderId = parent?.id ?: 0
                refresh()
            }
            onItemClickEvent = { item ->
                when (item.nodeType) {
                    PassRecord.TYPE_FOLDER -> {
                        _currentFolderId = item.id
                        refresh()
                    }
                    PassRecord.TYPE_RECORD -> {

                    }
                }
            }
            onDeleteEvent = { record ->
                val dao = PasswordsDao.getInstance(this@PasswordsRootFragment.requireContext().connectionSource)
                when (record.nodeType) {
                    PassRecord.TYPE_FOLDER -> dao.removeDir(record)
                    PassRecord.TYPE_RECORD -> dao.remove(record)
                }
                refresh()
            }
            onEditEvent = { record ->
                openEditor(record.id, record.nodeType, record)
            }
        }
        return _view
    }

    private fun openEditor(id: Long, type: Long, record: PassRecord? = null) {
        val editorFragment = PasswordsAddElementFragment()
        editorFragment.onOkClick = onOkClick

        val bundle = Bundle()
        bundle.putLong(KEY_ELEMENT_ID, id)
        bundle.putLong(KEY_PARENT_ID, _currentFolderId)
        bundle.putLong(KEY_TYPE, type)

        if (record != null) {
            when (type) {
                PassRecord.TYPE_FOLDER -> {
                    bundle.putString(KEY_DIR_NAME, record.title)
                    bundle.putString(KEY_DIR_NAME, record.text)
                }
                PassRecord.TYPE_RECORD -> {
                    bundle.putString(KEY_PASS_DESC, record.title)
                    val meta = gson.fromJson<PassRecordMetadata>(record.metadataJson, PassRecordMetadata::class.java)
                    bundle.putString(KEY_PASS_URL, meta.url)
                    bundle.putString(KEY_PASS_LOGIN, meta.login)
                }
            }
        }

        editorFragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder_dev_1, editorFragment)
            .addToBackStack("passEditor")
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuCreateNewFolder1 -> { openEditor(0, PassRecord.TYPE_FOLDER) }
            R.id.menuCreateNewPassword1 -> { openEditor(0, PassRecord.TYPE_RECORD) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        IconicsMenuInflaterUtil.inflate(inflater, this.requireContext(), R.menu.password_root_fragment_menu, menu)
        fixColorOfSearchBar(menu, R.id.passwords_fragment_search)
        val mItem: android.widget.SearchView = menu.findItem(R.id.passwords_fragment_search).actionView as android.widget.SearchView
        mItem.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean = false
            override fun onQueryTextSubmit(query: String): Boolean {

                mItem.clearFocus()
                _view?.hideKeyboard()
                return true
            }
        })
    }
}