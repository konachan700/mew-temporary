package com.mewhpm.mewsync.security

interface PasswordProvider {
    fun generatePassword(passId : Long, deviceId : String) : String
}