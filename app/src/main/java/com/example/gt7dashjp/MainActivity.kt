package com.example.gt7dashjp

import GT7PacketCounterUI
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import java.net.*

class MainActivity : ComponentActivity() {

    private var gt7Thread: GT7Communication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var packetCount by remember { mutableIntStateOf(0) }
            var rpm by remember { mutableFloatStateOf(0f) }

            GT7PacketCounterUI(
                packetCount = packetCount,
                rpm = rpm,
                onStart = { ip ->
                    gt7Thread = GT7Communication(ip) { count, currentRpm ->
                        packetCount = count
                        rpm = currentRpm
                    }
                    gt7Thread?.start()
                },
                onStop = {
                    gt7Thread?.stopCommunication()
                    gt7Thread = null
                }
            )
        }
    }
}

