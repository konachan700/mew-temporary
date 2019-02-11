package com.mewhpm.mewsync.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.Utils.CryptoUtils
import com.mewhpm.mewsync.Utils.VibroUtils
import kotlinx.android.synthetic.main.pincode_fragment.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

abstract class PinCodeBaseDialogFragment: androidx.fragment.app.DialogFragment() {
    companion object {
        const val EMPTY = ""
    }

    abstract fun onOkClick(pincode : String)
    private var _pinHash = EMPTY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pincode_fragment, container, false)
        view.pincodeBox1.text.clear()

        view.button11.onClick { addNumber("1") }
        view.button12.onClick { addNumber("2") }
        view.button13.onClick { addNumber("3") }
        view.button21.onClick { addNumber("4") }
        view.button22.onClick { addNumber("5") }
        view.button23.onClick { addNumber("6") }
        view.button31.onClick { addNumber("7") }
        view.button32.onClick { addNumber("8") }
        view.button33.onClick { addNumber("9") }
        view.button42.onClick { addNumber("0") }

        view.button41.onClick {
            VibroUtils.vibrate(context!!, 100)
            view.pincodeBox1.text.clear()
            _pinHash = EMPTY
        }

        view.button43.onClick {
            VibroUtils.vibrate(context!!, 100)
            if (_pinHash.contentEquals(EMPTY)) {
                view.incorrectPinWarning.visibility = View.VISIBLE
                view.incorrectPinWarning.text = getString(R.string.pincode_cannot_be_empty)
                return@onClick
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