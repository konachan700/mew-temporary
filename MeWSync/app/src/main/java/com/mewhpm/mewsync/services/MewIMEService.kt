package com.mewhpm.mewsync.services

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.dao.PasswordsDao
import com.mewhpm.mewsync.dao.connectionSource
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.ui.FixedHeightLinearLayout
import kotlinx.android.synthetic.main.x00_keyboard_view_real.view.*
import java.util.*


class MewIMEService : InputMethodService() {
    private var _currentFolderId = 0L

    override fun onCreateInputView(): View {
        val myView = View.inflate(this, com.mewhpm.mewsync.R.layout.x00_keyboard_view_real, null) as FixedHeightLinearLayout

        with (myView.listRVPasswordsKeyboard) {
            create()
            addItems(fillList(), true)
            onBackEvent = {
                val dao = PasswordsDao.getInstance(context.connectionSource)
                val parent = dao.getParent(_currentFolderId)
                _currentFolderId = parent?.id ?: 0
                addItems(fillList(), _currentFolderId == 0L)
            }
            onItemClickEvent = { item ->
                when (item.nodeType) {
                    PassRecord.TYPE_FOLDER -> {
                        _currentFolderId = item.id
                        addItems(fillList(), _currentFolderId == 0L)
                    }
                    PassRecord.TYPE_RECORD -> {

                    }
                }
            }
        }

        return myView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    private fun fillList() : List<PassRecord> {
        val defaultDevice = KnownDevicesDao.getInstance(applicationContext.connectionSource).getDefault()
        return if (defaultDevice == null)
            Collections.EMPTY_LIST as List<PassRecord>
        else
            PasswordsDao.getInstance(applicationContext.connectionSource).getAllChild(parentId = _currentFolderId, mac = defaultDevice.mac)

    }
}