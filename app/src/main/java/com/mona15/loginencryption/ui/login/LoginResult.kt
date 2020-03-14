package com.mona15.loginencryption.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult (
     val success:LoggedInUserView? = null,
     val error:Int? = null
)
