package com.mewhpm.mewsync.ui.pinpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import kotlinx.android.synthetic.main.x01_pincode_fragment.view.*

class PinCodeCreateDialogFragment : PinCodeBaseDialogFragment() {
    var onPinCodeCreated: (pinHash: String) -> Unit = {}
    private val _hashList: ArrayList<String> = ArrayList(2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view!!.incorrectPinWarning.visibility = View.VISIBLE
        view.incorrectPinWarning.text = getString(R.string.please_create_new_pincode)
        return view
    }

    override fun onOkClick(pincode : String) {
        //Log.e("onOkClick", "pincode ${_hashList.size}: $pincode")
        _hashList.add(pincode)
        verify()
    }

    private fun verify() {
        when (_hashList.size) {
            1 -> {
                view!!.incorrectPinWarning.visibility = View.VISIBLE
                view!!.incorrectPinWarning.text = getString(R.string.please_confirm_your_pincode)
                view!!.pincodeBox1.text.clear()
            }
            2 -> {
                if (_hashList[0].contentEquals(_hashList[1])) {
                    view!!.incorrectPinWarning.visibility = View.GONE
                    view!!.pincodeBox1.text.clear()
                    onPinCodeCreated.invoke(_hashList[0])
                    this@PinCodeCreateDialogFragment.dismiss()
                } else {
                    view!!.incorrectPinWarning.visibility = View.VISIBLE
                    view!!.incorrectPinWarning.text = getString(R.string.entered_pincodes_does_not_match)
                    view!!.pincodeBox1.text.clear()
                }
                _hashList.clear()
            }
        }
    }
}