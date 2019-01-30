package com.mewhpm.mewsync.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mewhpm.mewsync.R
import com.mewhpm.mewsync.Utils.Crypto
import kotlinx.android.synthetic.main.pincode_fragment.*
import kotlinx.android.synthetic.main.pincode_fragment.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class PinCodeDialogFragment : DialogFragment() {
    companion object {
        const val SALT = "243fg76g76*t&*v*&n)SHmk((r7tjr5fr7ngn567)N86F97V65V54s$#X3X437S46D&vb0NB86%^c*%"
    }

    var closeListener: (hash: String) -> Boolean = { false }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pincode_fragment, container, false)
        view.pincodeBox1.text.clear()
        view.incorrectPinWarning.visibility = View.GONE

        view.button11.onClick { addNumber("1") }
        view.button12.onClick { addNumber("2") }
        view.button13.onClick { addNumber("3") }
        view.button21.onClick { addNumber("4") }
        view.button21.onClick { addNumber("5") }
        view.button23.onClick { addNumber("6") }
        view.button31.onClick { addNumber("7") }
        view.button32.onClick { addNumber("8") }
        view.button33.onClick { addNumber("9") }
        view.button42.onClick { addNumber("0") }

        view.button11.onClick {
            if (view.pincodeBox1.text.isEmpty()) return@onClick

            val newText = view?.pincodeBox1?.text?.dropLast(2)
            view?.pincodeBox1?.text?.clear()
            view?.pincodeBox1?.text?.append(newText)
        }

        view.button11.onClick {
            val hash = Crypto().sha256(view.pincodeBox1.text.toString() + SALT)
            if (closeListener.invoke(hash)) {
                view.incorrectPinWarning.visibility = View.GONE
                this@PinCodeDialogFragment.dismiss()
            } else {
                view.incorrectPinWarning.visibility = View.VISIBLE
                view.pincodeBox1.text.clear()
            }
        }

        return view
    }

    private fun addNumber(number: String) {
        view?.pincodeBox1?.append("$number ")
    }
}