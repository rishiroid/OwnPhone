/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.rishiz.phone.model

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.telecom.CallAttributesCompat
import androidx.core.telecom.CallsManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The central repository that keeps track of the current call and allows to register new calls.
 *
 * This class contains the main logic to integrate with Telecom SDK.
 *
 * @see registerCall
 */
@RequiresApi(Build.VERSION_CODES.O)
class CallRepo(  ) {

    companion object {
        var instance: CallRepo? = null
            private set

        /**
         * This does not illustrate best practices for instantiating classes in Android but for
         * simplicity we use this create method to create a singleton with the CallsManager class.
         */
        fun create(context: Context): CallRepo? {
            Log.d("MPB", "New instance")
            check(instance == null) {
                "CallRepository instance already created"
            }

//            // Create the Jetpack Telecom entry point
//            val callsManager = CallsManager(context).apply {
//                // Register with the telecom interface with the supported capabilities
//                registerAppWithTelecom(
//                    capabilities = CallsManager.CAPABILITY_SUPPORTS_CALL_STREAMING and
//                            CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING,
//                )
//            }

//            return CallRepo(
//                callsManager = callsManager,
//            ).also {
//                instance = it
//            }
            return CallRepo().also {
                instance=it
            }
        }
    }

    // Keeps track of the current TelecomCall state
    private val _currentCall: MutableStateFlow<CallState> = MutableStateFlow(CallState.None)
    val currentCall = _currentCall.asStateFlow()

    /**
     * Register a new call with the provided attributes.
     * Use the [currentCall] StateFlow to receive status updates and process call related actions.
     */
    suspend fun registerCall(displayName: String, address: Uri, isIncoming: Boolean) {
        // For simplicity we don't support multiple calls
        check(_currentCall.value !is CallState.Registered) {
            "There cannot be more than one call at the same time."
        }

        // Create the call attributes
        val attributes = CallAttributesCompat(
            displayName = displayName,
            address = address,
            direction = if (isIncoming) {
                CallAttributesCompat.DIRECTION_INCOMING
            } else {
                CallAttributesCompat.DIRECTION_OUTGOING
            },
            callType = CallAttributesCompat.CALL_TYPE_AUDIO_CALL,
            callCapabilities = (CallAttributesCompat.SUPPORTS_SET_INACTIVE
                    or CallAttributesCompat.SUPPORTS_STREAM
                    or CallAttributesCompat.SUPPORTS_TRANSFER),
        )

        // Creates a channel to send actions to the call scope.
        val actionSource = Channel<CallAction>()
        // Register the call and handle actions in the scope
        // Update the state to registered with default values while waiting for Telecom updates
        _currentCall.value = CallState.Registered(
            isActive = false,
            isOnHold = false,
            callAttributes = attributes,
            isMuted = false,
            errorCode = null,
            currentCallEndpoint = null,
            availableCallEndpoints = emptyList(),
            actionSource = actionSource,
        )
        try {


        } finally {
//            _currentCall.value = CallState.None
        }
    }
}