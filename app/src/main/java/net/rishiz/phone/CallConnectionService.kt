package net.rishiz.phone

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.rishiz.phone.model.CallAction
import net.rishiz.phone.model.CallRepo
import net.rishiz.phone.model.CallState


class CallConnectionService : ConnectionService() {
    companion object {
        private val TAG = CallConnectionService::class.java.canonicalName
    }

    private lateinit var callRepo: CallRepo
    private var connection: CallConnection? = null
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        callRepo =
            (CallRepo.instance ?: CallRepo.create(this@CallConnectionService))!!
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }


    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Toast.makeText(this@CallConnectionService, "onCreateOutgoingConnection", Toast.LENGTH_SHORT)
            .show()
        Log.d(TAG, "onCreateOutgoingConnection")
        val bundle = request!!.extras
        val name = bundle.getString("Name")
        val callType = bundle.getString("CALLTYPE")
        val address = request.address

        connection = CallConnection()

        connection?.connectionProperties = Connection.PROPERTY_SELF_MANAGED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            connection?.connectionCapabilities = Connection.AUDIO_CODEC_AMR_WB
        }

        connection?.setCallerDisplayName(name, TelecomManager.PRESENTATION_ALLOWED)
        connection?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        connection?.setInitialized()
//        connection?.videoState=VideoProfile.STATE_AUDIO_ONLY
        connection?.setActive()
        scope.launch {
            registerCall(name!!, address, false)
        }
        return connection!!

    }

    //The telecom subsystem calls this method in response
// to your app calling when outgoing call cannot be placed
    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        Toast.makeText(
            this@CallConnectionService,
            "onCreateOutgoingConnectionFailed:$request",
            Toast.LENGTH_SHORT
        ).show()
//        viewModel.setCallState(CallState.None)
        Log.d(TAG, "connectionManagerPhoneAccount:${connectionManagerPhoneAccount?.id}")
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    //    The telecom subsystem calls this method when your app calls
//    the addNewIncomingCall(PhoneAccountHandle, Bundle) method
//    to inform the system of a new incoming call in your app.
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request)
    }

    //The telecom subsystem calls this method when your app calls
    //the addNewIncomingCall(PhoneAccountHandle, Bundle) method
    //to inform the system of a new incoming call in your app.
    //but the incoming call isn't permitted
    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    private suspend fun registerCall(name: String, address: Uri, isIncoming: Boolean) {
        // If we have an ongoing call ignore command
        if (callRepo.currentCall.value is CallState.Registered) {
            return
        }

        scope.launch {

            launch {
                Log.d(TAG, "registerCall")
                // Register the call with the Telecom stack
                callRepo.registerCall(name, address, isIncoming)
            }

            if (!isIncoming) {
                Log.d(TAG, "reg" + callRepo.currentCall.value)
                (callRepo.currentCall.value as? CallState.Registered)?.processAction(
                    CallAction.Activate,
                )
                Log.d(TAG, "reg" + callRepo.currentCall.value)
            }
        }
    }

}


