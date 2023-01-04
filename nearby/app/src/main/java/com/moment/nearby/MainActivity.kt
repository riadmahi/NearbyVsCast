package com.moment.nearby

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.moment.nearby.ui.theme.NearbyTheme


private var isBluetoothEnabled = mutableStateOf(false)

val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
} else {
    listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NearbyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    requestPermissionLauncher.launch(permissionsList.toTypedArray())
                    MainScreen()
                }
            }
        }
    }
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                isBluetoothEnabled.value = it.value
            }
        }
}
private var endPointId: String = ""
@Composable
fun MainScreen() {
    Log.d(TAG, "MainScreen: enabled blu ${isBluetoothEnabled.value}")
    if(isBluetoothEnabled.value) {
        val context = LocalContext.current
        val discoveryOptions: DiscoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery("com.moment.nearbytv", object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
                    Log.d(TAG, "onEndpointFound: $p0  a découvert $p1")
                    Nearby.getConnectionsClient(context).requestConnection("com.moment.nearby", p0,
                    object: ConnectionLifecycleCallback(){
                        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                            Nearby.getConnectionsClient(context)
                                .acceptConnection(p0, mPayloadCallback);
                        }

                        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                            when (p1.getStatus().getStatusCode()) {
                                ConnectionsStatusCodes.STATUS_OK -> {
                                    Log.d(TAG, "onConnectionResult: ok")
                                    endPointId = p0
                                }
                                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                                    Log.d(TAG, "onConnectionResult: rejected")

                                }
                                ConnectionsStatusCodes.STATUS_ERROR -> {
                                    Log.d(TAG, "onConnectionResult: error")
                                }
                                else -> {
                                    Log.d(TAG, "onConnectionResult: nothing")
                                }
                            }
                        }
                        override fun onDisconnected(p0: String) {
                            Log.d(TAG, "onDisconnected: disconected")
                        }
                    })
                }

                override fun onEndpointLost(p0: String) {
                    Log.d(TAG, "onEndpointLost: $p0 à rien trouvé")
                }

            }, discoveryOptions)
            .addOnSuccessListener { unused: Void? -> Log.d(TAG, "MainScreen: success !") }
            .addOnFailureListener { e: Exception? -> }
    }

    Box(modifier= Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        val context = LocalContext.current
        Button(onClick = {
            sendPayLoad(context = context, endPointId = endPointId)  },
            modifier= Modifier
                .width(300.dp)
                .height(70.dp)
        ) {
            Text(text = "Augmenter le compteur de la connectTV")
        }
    }

}
private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
    override fun onPayloadReceived(s: String, payload: Payload) { }
    override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) { }
}

private fun sendPayLoad(context: Context, endPointId: String) {
    val bytesPayload = Payload.fromBytes(java.lang.String.valueOf(KeyEvent.KEYCODE_ENTER).toByteArray())
    Nearby.getConnectionsClient(
        context
    ).sendPayload(endPointId, bytesPayload).addOnSuccessListener { }.addOnFailureListener { }
}
