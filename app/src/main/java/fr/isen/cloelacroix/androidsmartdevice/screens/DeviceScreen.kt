package fr.isen.cloelacroix.androidsmartdevice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeviceScreen(
    name: String,
    address: String,
    rssi: Int,
    onBack: () -> Unit,
    onConnectClick: () -> Unit,
    connectionStatus: String,
    isConnected: Boolean,
    ledStates: List<Boolean>,
    onLedToggle: (Int) -> Unit,
    isSubscribedButton1: Boolean,
    isSubscribedButton3: Boolean,
    onSubscribeToggleButton1: (Boolean) -> Unit,
    onSubscribeToggleButton3: (Boolean) -> Unit,
    counterButton1: Int,
    counterButton3: Int,
    onResetCounter: () -> Unit
) {
    val ledColors = listOf(
        Color(0xFF1976D2), // LED 1 - Bleu
        Color(0xFF4CAF50), // LED 2 - Vert
        Color(0xFFF44336)  // LED 3 - Rouge
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AndroidSmartDevice",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(36.dp),
            color = Color(0xFFE61F93),
            textAlign = TextAlign.Center

        )

        if (!isConnected) {

                Column(
                    modifier = Modifier
                        .padding(36.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Appareil détecté", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE61F93))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nom : $name", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Adresse : $address", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("RSSI : $rssi dBm", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(" $connectionStatus", fontSize = 16.sp, color = Color(0xFFE61F93))
                }


            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onConnectClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292))
            ) {
                Text("Se connecter", color = Color.White, fontSize = 16.sp)
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ledStates.forEachIndexed { index, isOn ->
                    val color = ledColors.getOrNull(index) ?: Color.Gray
                    Button(
                        onClick = { onLedToggle(index) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOn) Color(0xFFE61F93)  else  Color(0xFFF8BBD0)
                        ),
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "LED ${index + 1}",
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSubscribedButton3,
                    onCheckedChange = { onSubscribeToggleButton3(it) }
                )
                Text("Notificatins bouton 1")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSubscribedButton1,
                    onCheckedChange = { onSubscribeToggleButton1(it) }
                )
                Text("Notifications bouton 3")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Compteur bouton 1 : $counterButton3", fontSize = 16.sp)
            Text("Compteur bouton 3 : $counterButton1", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onResetCounter,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE61F93) )
            ) {
                Text("Réinitialiser les compteurs", color = Color.White)
            }
        }
    }
}