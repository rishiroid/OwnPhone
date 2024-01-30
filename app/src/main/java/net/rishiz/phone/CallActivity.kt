package net.rishiz.phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import net.rishiz.phone.call.CallScreen
import net.rishiz.phone.model.CallRepo

class CallActivity : ComponentActivity() {

    companion object {
        fun start(context: Context, bundle: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .setData(bundle)
                .let(context::startActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The repo contains all the call logic and communication with the Telecom SDK.
         val callRepo =
            CallRepo.instance ?: CallRepo.create(this@CallActivity)
        setContent {
            MaterialTheme {
                Surface(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    // Show the in-call screen
                    CallScreen(callRepo!!) {
                        // If we receive that the called finished, finish the activity
                        finishAndRemoveTask()
                        Log.d("TelecomCallActivity", "Call finished. Finishing activity")
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        // Force the service to update in case something change like Mic permissions.

    }
}