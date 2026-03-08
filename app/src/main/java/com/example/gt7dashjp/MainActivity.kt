package com.example.gt7dashjp

import GT7PacketCounterUI
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    private var gt7Thread: GT7Communication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Hide status bar and navigation bar (full screen)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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

