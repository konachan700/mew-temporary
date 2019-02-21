package com.mewhpm.mewsync.fragments

import android.os.Bundle
import android.view.*
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.utils.hideKeyboard
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import kotlinx.android.synthetic.main.x02_fragment_add_directory.view.*
import kotlinx.android.synthetic.main.x02_fragment_add_password.view.*
import org.jetbrains.anko.support.v4.toast

class PasswordsAddElementFragment : androidx.fragment.app.Fragment() {
    companion object {
        const val PRIMARY_TEXT_MAX_LEN = 40

        const val KEY_TYPE = "dialogType"
        const val KEY_ELEMENT_ID = "elementId"
        const val KEY_PARENT_ID = "parentId"

        const val KEY_DIR_NAME = "editTextDirectoryName"
        const val KEY_DIR_DESC = "editTextDirectoryDescription"

        const val KEY_PASS_URL = "editTextPassURL"
        const val KEY_PASS_DESC = "editTextPassDescription"
        const val KEY_PASS_LOGIN = "editTextPassLogin"
    }

    private var _type = PassRecord.TYPE_FOLDER
    private var _view: View? = null
    private var _currentId = 0L
    private var _parentId = 0L

    var onOkClick : (bundle: Bundle) -> Unit = {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuCreateNewElementCancel -> {
                _view!!.hideKeyboard()
                activity!!.supportFragmentManager.popBackStack()
            }
            R.id.menuCreateNewElementOk -> {
                val bundle = Bundle()
                bundle.putLong(KEY_TYPE, _type)
                bundle.putLong(KEY_ELEMENT_ID, _currentId)
                bundle.putLong(KEY_PARENT_ID, _parentId)

                when (_type) {
                    PassRecord.TYPE_FOLDER -> {
                        val dirName = _view!!.editTextDirectoryName.text.toString()
                        if (dirName.isBlank()) {
                            toast("Directory name can't be blank").show()
                            return super.onOptionsItemSelected(item)
                        }
                        if (dirName.length > PRIMARY_TEXT_MAX_LEN) {
                            toast("Directory name must be a 1-$PRIMARY_TEXT_MAX_LEN chars").show()
                            return super.onOptionsItemSelected(item)
                        }
                        bundle.putString(KEY_DIR_NAME, dirName)
                        bundle.putString(KEY_DIR_DESC, _view!!.editTextDirectoryDescription.text.toString())
                        onOkClick.invoke(bundle)

                        _view!!.hideKeyboard()
                        activity!!.supportFragmentManager.popBackStack()
                    }
                    PassRecord.TYPE_RECORD -> {
                        val desc = _view!!.editTextPassDescription.text.toString()
                        if (desc.isBlank()) {
                            toast(getString(R.string.password_not_be_blank)).show()
                            return super.onOptionsItemSelected(item)
                        }
                        if (desc.length > PRIMARY_TEXT_MAX_LEN) {
                            toast("Password description must be a 1-$PRIMARY_TEXT_MAX_LEN chars").show()
                            return super.onOptionsItemSelected(item)
                        }

                        bundle.putString(KEY_PASS_URL, _view!!.editTextPassURL.text.toString())
                        bundle.putString(KEY_PASS_DESC, desc)
                        bundle.putString(KEY_PASS_LOGIN, _view!!.editTextPassLogin.text.toString())
                        onOkClick.invoke(bundle)

                        _view!!.hideKeyboard()
                        activity!!.supportFragmentManager.popBackStack()
                    }
                    else -> throw IllegalArgumentException("PasswordsAddElementFragment::onOptionsItemSelected - Strange error")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        IconicsMenuInflaterUtil.inflate(inflater, this.requireContext(), R.menu.password_add_element_fragment_menu, menu)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.setHasOptionsMenu(true)

        _currentId = arguments!!.getLong(KEY_ELEMENT_ID, 0L)
        _parentId = arguments!!.getLong(KEY_PARENT_ID, 0L)
        _type = arguments!!.getLong(KEY_TYPE, PassRecord.TYPE_FOLDER)

        when (_type) {
            PassRecord.TYPE_FOLDER -> {
                _view = inflater.inflate(R.layout.x02_fragment_add_directory, container, false)
                _view!!.editTextDirectoryName.setText(arguments!!.getString(KEY_DIR_NAME, ""))
                _view!!.editTextDirectoryDescription.setText(arguments!!.getString(KEY_DIR_DESC, ""))

            }
            PassRecord.TYPE_RECORD -> {
                _view = inflater.inflate(R.layout.x02_fragment_add_password, container, false)
                _view!!.editTextPassURL.setText(arguments!!.getString(KEY_PASS_URL, ""))
                _view!!.editTextPassLogin.setText(arguments!!.getString(KEY_PASS_LOGIN, ""))
                _view!!.editTextPassDescription.setText(arguments!!.getString(KEY_PASS_DESC, ""))
            }
            else -> throw IllegalArgumentException("PasswordsAddElementFragment::onCreateView - Strange error")
        }

        return _view
    }
}