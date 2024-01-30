package net.rishiz.phone

import android.net.Uri
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.util.Log

class CallConnection() :Connection() {

    val TAG=CallConnection::class.java.canonicalName

    /**
     * This callback is triggered whenever there is a change in the call state.
     * The onStateChanged method provides the new state as an integer parameter.
     */

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        Log.d(TAG,"onStateChanged")
        when(state){
            STATE_NEW-> Log.d(TAG,"STATE_NEW")
            STATE_INITIALIZING->Log.d(TAG,"STATE_INITIALIZING")
            STATE_RINGING->Log.d(TAG,"STATE_RINGING")
            STATE_DIALING->Log.d(TAG,"STATE_DIALING")
            STATE_ACTIVE->Log.d(TAG,"STATE_ACTIVE")
            STATE_HOLDING->Log.d(TAG,"STATE_HOLDING")
            STATE_DISCONNECTED->Log.d(TAG,"STATE_DISCONNECTED")
        }
    }

    /**
     * This callback is triggered when it's time to show the incoming call UI to the user.
     * It allows you to customize the UI when an incoming call is received.
     */
    //show incoming call UI.
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
    }

    override fun onHold() {
        super.onHold()
    }

    override fun onUnhold() {
        super.onUnhold()
    }

    /**
     * This callback is triggered when you want to accept or answer an incoming call.
     * It is typically called in response to user interaction when the user decides to accept the incoming call.
     */
    override fun onAnswer() {
        super.onAnswer()

        Log.d(TAG,"onAnswer")
    }


    override fun onReject() {
        super.onReject()
        Log.d(TAG,"onReject")
    }

    /**
     * This callback is triggered when a call is being disconnected.
     * It is called when you initiate the disconnection of a call or when the call naturally ends.
     */
    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(TAG,"onDisconnect")
    }

    override fun onAbort() {
        super.onAbort()
        Log.d(TAG,"onAbort")
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        Log.d(TAG,"onAnswer")
    }


    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(TAG,"onCallEvent")
    }

    override fun onMuteStateChanged(isMuted: Boolean) {
        super.onMuteStateChanged(isMuted)
    }

    override fun onCallAudioStateChanged(state: CallAudioState?) {
        super.onCallAudioStateChanged(state)
        Log.d(TAG,"onCallAudioStateChanged"+state)
    }

}