package com.moment.adbremotecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moment.adbremotecontrol.ui.theme.ADBRemoteControlTheme
import com.anymote.ui.AnymoteSender
import com.google.android.gms.cast.tv.CastReceiverContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ADBRemoteControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(Modifier.fillMaxSize()){
                        DeviceManager()

                    }
                }
            }
        }
    }
}

@Composable
fun DeviceManager(){
    val context = CastReceiverContext.getInstance()
    context.sendMessage("urn:x-cast:1")
    Button(onClick = {  }) {
        Text(text = "Send UP")
    }

}
