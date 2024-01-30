package net.rishiz.phone.model

import android.os.ParcelUuid

import android.telecom.DisconnectCause

sealed class CallAction {

    data object Answer : CallAction()

    data class Disconnect(val cause: DisconnectCause) : CallAction()

    data object Hold : CallAction()

    data object Activate : CallAction()

    data class ToggleMute(val isMute: Boolean) : CallAction()

    data class SwitchAudioEndpoint(val endpointId: ParcelUuid) : CallAction()

    data class TransferCall(val endpointId: ParcelUuid) : CallAction()

}