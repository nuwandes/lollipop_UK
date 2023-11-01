package org.nghru_uk.ghru

import android.app.Activity

interface LifecycleDelegate {

    fun onAppBackgrounded()
    fun onAppForegrounded()
    fun onScreenLocked()

}

interface LogoutDelegate {

    fun startTimer(context: Activity?)
    fun stopTimer()


}