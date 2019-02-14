package com.mewhpm.mewsync.ui.pinpad

import androidx.fragment.app.FragmentManager

class Pinpad {
    companion object {
        private val verifyDialogFragment: PinCodeVerifyDialogFragment = PinCodeVerifyDialogFragment()
        private val createDialogFragment: PinCodeCreateDialogFragment = PinCodeCreateDialogFragment()

        fun verify(fm: FragmentManager, onPincodeEntered: (pincode: String) -> Boolean) {
            verifyDialogFragment.onPincodeEntered = onPincodeEntered
            verifyDialogFragment.show(fm, "pinpad-verify")
        }

        fun create(fm: FragmentManager, onPinCodeCreated: (pinHash: String) -> Unit) {
            createDialogFragment.onPinCodeCreated = onPinCodeCreated
            createDialogFragment.show(fm, "pinpad-create")
        }
    }
}

fun androidx.fragment.app.Fragment.verifyPin(onPincodeEntered: (pincode: String) -> Boolean) {
    Pinpad.verify(fragmentManager!!, onPincodeEntered)
}

fun androidx.fragment.app.Fragment.createPin(onPinCodeCreated: (pinHash: String) -> Unit) {
    Pinpad.create(fragmentManager!!, onPinCodeCreated)
}

fun androidx.appcompat.app.AppCompatActivity.verifyPin(onPincodeEntered: (pincode: String) -> Boolean) {
    com.mewhpm.mewsync.ui.pinpad.Pinpad.verify(supportFragmentManager, onPincodeEntered)
}