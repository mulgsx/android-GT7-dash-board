import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GT7PacketCounterUI(
    packetCount: Int,
    rpm: Float,
    onStart: (String) -> Unit,
    onStop: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var ipAddress by remember { mutableStateOf("") }
    var ipError by remember { mutableStateOf(false) }
    val ipRegex = Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$""")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Settings", fontSize = 22.sp)
                    HorizontalDivider()

                    OutlinedTextField(
                        value = ipAddress,
                        onValueChange = {
                            ipAddress = it
                            ipError = false
                        },
                        label = { Text("PlayStation IP") },
                        isError = ipError,
                        supportingText = if (ipError) {
                            { Text("Please enter a valid IP address") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (ipRegex.matches(ipAddress)) {
                                ipError = false
                                scope.launch { drawerState.close() }
                                onStart(ipAddress)
                            } else {
                                ipError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Receiving")
                    }

                    OutlinedButton(
                        onClick = {
                            onStop()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Stop Receiving")
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GT7 Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Settings"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { innerPadding ->
            // Main dashboard (landscape)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RPM display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RPM", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        text = rpm.toInt().toString(),
                        fontSize = 64.sp,
                        color = Color.Black
                    )
                }

                VerticalDivider(modifier = Modifier.height(120.dp))

                // Packet counter
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Packets", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        text = packetCount.toString(),
                        fontSize = 48.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
