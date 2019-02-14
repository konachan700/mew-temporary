package com.mewhpm.mewsync.ui.pinpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import kotlinx.android.synthetic.main.x01_pincode_fragment.view.*

class PinCodeVerifyDialogFragment : PinCodeBaseDialogFragment() {
    var onPincodeEntered: (pincode: String) -> Boolean = { true }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view!!.incorrectPinWarning.visibility = View.GONE
        view.pincodeBox1.text.clear()
        return view
    }

    override fun onOkClick(pincode : String) {
        if (onPincodeEntered.invoke(pincode)) {
            view!!.incorrectPinWarning.visibility = View.GONE
            view!!.pincodeBox1.text.clear()
            this@PinCodeVerifyDialogFragment.dismiss()
        } else {
            view!!.incorrectPinWarning.visibility = View.VISIBLE
            view!!.incorrectPinWarning.text = getString(R.string.incorrect_pincode)
            view!!.pincodeBox1.text.clear()
        }
    }
}