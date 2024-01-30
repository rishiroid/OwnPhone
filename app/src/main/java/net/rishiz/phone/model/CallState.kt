package net.rishiz.phone.model

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.telecom.CallAttributes
import android.telecom.DisconnectCause
import androidx.core.telecom.CallAttributesCompat
import androidx.core.telecom.CallEndpointCompat
import kotlinx.coroutines.channels.Channel


sealed class CallState {
    /**
     * There is no current or past calls in the stack
     */
    data object None : CallState()


    /**
     * Repr)esents a registered call with the telecom stack with the values provided by the
     * Telecom SDK
     */
    data class Registered(
//        val id: ParcelUuid,
        val callAttributes: CallAttributesCompat,
        val isActive: Boolean,
        val isOnHold: Boolean,
        val isMuted: Boolean,
        val errorCode: Int?,
        val currentCallEndpoint: CallEndpointCompat?,
        val availableCallEndpoints: List<CallEndpointCompat>,
        internal val actionSource: Channel<CallAction>
    ) : CallState() {
        /**
         * @return true if it's an incoming registered call, false otherwise
         */
        @TargetApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        fun isIncoming() = callAttributes.direction == CallAttributes.DIRECTION_INCOMING

        /**
         * Sends an action to the call session. It will be processed if it's still registered.
         *
         * @return true if the action was sent, false otherwise
         */
        fun processAction(action: CallAction) = actionSource.trySend(action).isSuccess

    }
    /**
     * Represent a previously registered call that was disconnected
     */
    data class Unregistered(
//        val id:ParcelUuid,
        val callAttributes: CallAttributesCompat,
        val disconnectCause: DisconnectCause
    ):CallState()

//    data object Connecting:CallState()

}