package net.rishiz.phone

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import net.rishiz.phone.ui.theme.PhoneTheme


class MainActivity : ComponentActivity() {
    //    private lateinit var phoneAccountHandle: PhoneAccountHandle
    private val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// The repo contains all the call logic and communication with the Telecom SDK.

// Set the right flags for a call type activity.
        setContent {
            PhoneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var phoneNumber by remember {
                        mutableStateOf(" ")
                    }


                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        OutlinedTextField(value = phoneNumber, onValueChange = {
                            // Basic input validation, allowing only numeric characters
                            phoneNumber = it.filter { char -> char.isDigit() }

                        }, label = { Text("Enter number") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FloatingActionButton(onClick = {
                            val phonNumberUri: Uri = Uri.parse("tel:$phoneNumber")
                            makeCall(this@MainActivity, phonNumberUri)

                        }) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "call")
                        }
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        requestRole(this@MainActivity)
    }


    companion object {
        const val REQUEST_ID = 1
    }

    @SuppressLint("NewApi")
    fun requestRole(context: Context) {
        val roleManager = context.getSystemService(ROLE_SERVICE) as RoleManager?
        val intent = roleManager!!.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun makeCall(context: Context, phoneNumberUri: Uri) {

        val telecomManager = context.getSystemService(TelecomManager::class.java)

        //set account handler and pass this through bundle
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(context, CallConnectionService::class.java), "CallConnectionService"
        )
        //Create Phone account ,this label is display while selecting calling account
        // In mobile for enabling connection service
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, " rishiz calling")
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .build()
        telecomManager.registerPhoneAccount(phoneAccount)

        val extras = Bundle().apply {
            putString("Name", "rishiz")
            putString("CALL TYPE", "Outgoing")
        }

        // Build the call details
        val callDetails = Bundle().apply {
            // Add call details as needed
            putString(TelecomManager.EXTRA_CALL_SUBJECT, "Outgoing Call Subject")
            putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            putParcelable(TelecomManager.ACTION_PHONE_ACCOUNT_REGISTERED,phoneAccount)
            putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE,VideoProfile.STATE_BIDIRECTIONAL)
            putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extras)
            putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE,false)
            //putParcelable(TelecomManager.EXTRA_RINGING_CALL)
        }


        //Check permission and request if not granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        } else {
            telecomManager.placeCall(phoneNumberUri, callDetails)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PhoneTheme {

    }
}

