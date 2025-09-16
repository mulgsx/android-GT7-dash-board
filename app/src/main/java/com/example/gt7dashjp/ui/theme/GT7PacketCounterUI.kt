import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.graphics.Color

import com.example.gt7dashjp.R

@Composable
fun GT7PacketCounterUI(
    packetCount: Int,
    rpm: Float,
    onStart: (String) -> Unit,
    onStop: () -> Unit
) {
    var ipAddress by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("PlayStation IP") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { onStart(ipAddress) }) {
            Text("受信開始")
        }

        Button(onClick = onStop) {
            Text("受信停止")
        }

        Text("受信パケット数: $packetCount", fontSize = 20.sp)
        Row {
            Text(
                text = "回転数:",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = rpm.toInt().toString(),
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = " RPM",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.alignByBaseline()
            )
        }
        //Text("回転数: ${rpm.toInt()} RPM", fontSize = 20.sp)
    }
}

@Composable
fun SevenSegmentText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 64.sp,
        fontFamily = FontFamily(Font(R.font.ds_digital)),
        color = Color.Green,
        modifier = modifier
    )
}
