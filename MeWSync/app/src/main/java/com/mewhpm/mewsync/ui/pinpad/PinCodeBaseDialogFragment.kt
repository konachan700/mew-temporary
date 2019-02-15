package com.mewhpm.mewsync.ui.pinpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.utils.VibroUtils
import kotlinx.android.synthetic.main.x01_pincode_fragment.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

abstract class PinCodeBaseDialogFragment: androidx.fragment.app.DialogFragment() {
    companion object {
        const val EMPTY = ""
    }

    abstract fun onOkClick(pincode : String)
    private var _pinHash = EMPTY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.x01_pincode_fragment, container, false)
        view.pincodeBox1.text.clear()

        dialog?.window?.setBackgroundDrawableResource(R.drawable.fragment_dialog_bg)

        view.button11.setOnClickListener { addNumber("1") }
        view.button12.setOnClickListener { addNumber("2") }
        view.button13.setOnClickListener { addNumber("3") }
        view.button21.setOnClickListener { addNumber("4") }
        view.button22.setOnClickListener { addNumber("5") }
        view.button23.setOnClickListener { addNumber("6") }
        view.button31.setOnClickListener { addNumber("7") }
        view.button32.setOnClickListener { addNumber("8") }
        view.button33.setOnClickListener { addNumber("9") }
        view.button42.setOnClickListener { addNumber("0") }

        view.button41.setOnClickListener {
            VibroUtils.vibrate(context!!, 100)
            view.pincodeBox1.text.clear()
            _pinHash = EMPTY
        }

        view.button43.setOnClickListener {
            VibroUtils.vibrate(context!!, 100)
            if (_pinHash.contentEquals(EMPTY)) {
                view.incorrectPinWarning.visibility = View.VISIBLE
                view.incorrectPinWarning.text = getString(R.string.pincode_cannot_be_empty)
                return@setOnClickListener
            }
            onOkClick(_pinHash)
            _pinHash = EMPTY
        }
        return view
    }

    private fun addNumber(number: String) {
        VibroUtils.vibrate(context!!, 100)
        val pin = StringBuilder()
            .append(_pinHash)
            .append(number)
            .append(CryptoUtils.getUniqueSalt())
            .append(number)
            .toString()
        _pinHash = CryptoUtils.sha256(pin)
        view!!.pincodeBox1.append("X ")
    }
}