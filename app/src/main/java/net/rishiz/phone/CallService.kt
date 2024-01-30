package net.rishiz.phone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

class CallService : InCallService() {

    private val TAG = CallService::class.java.canonicalName


    @SuppressLint("NewApi")
    override fun onCallAdded(call: Call) {
        Log.d(TAG, "onCallAdded:${call.details.callerDisplayName}")
        CallActivity.start(this, call)

    }

    override fun onCallRemoved(call: Call) {
        Log.d(TAG, "onCallRemoved:$call")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }
}