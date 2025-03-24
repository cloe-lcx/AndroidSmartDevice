package fr.isen.cloelacroix.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.cloelacroix.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ScanActivity : ComponentActivity() {
    private val scanResults = mutableStateListOf<BleDevice>()
    private var isScanning by mutableStateOf(false)
    private var progress by mutableStateOf(0f)  // Progression du scan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScanScreen(
                        onScanClick = { if (isScanning) stopScan() else startScan() },
                        scanResults = scanResults,
                        isScanning = isScanning,
                        progress = progress,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun startScan() {
        isScanning = true
        progress = 0f
        scanResults.clear()  // Effacer les résultats précédents
        // Ajouter les faux appareils
        scanResults.addAll(listOf(
            BleDevice("Device Unknown", "47:28:A3:7C:E2:BE", -84),
            BleDevice("Labo_IoT", "00:80:E1:26:6F:6E", -61),
            BleDevice("Device Unknown", "53:C7:E7:5E:D8:2E", -88),
            BleDevice("Device Unknown", "4A:6C:22:2E:ED:DE", -97),
            BleDevice("Device Unknown", "55:F7:CF:C6:17:70", -83),
            BleDevice("Device Unknown", "FF:CC:05:0F:05:00", -86)
        ))
        // Simuler le progrès
        simulateProgress()
    }

    private fun stopScan() {
        isScanning = false
    }

    // Simuler la progression du scan
    private fun simulateProgress() {
        // Augmenter la progression toutes les 100ms
        for (i in 1..100) {
            android.os.Handler().postDelayed({
                progress = i / 100f
                if (i == 100) stopScan()
            }, (i * 100).toLong())
        }
    }
}

@Composable
fun ScanScreen(
    onScanClick: () -> Unit,
    scanResults: List<BleDevice>,
    isScanning: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de l'écran
        Text(
            text = "AndroidSmartDevice",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Bouton pour lancer/arrêter le scan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isScanning) {
                Text(text = "Scan BLE en cours", modifier = Modifier.padding(end = 8.dp), fontSize = 22.sp)

                IconButton(onClick = { onScanClick() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Stop", tint = Color.Red, modifier = Modifier.size(32.dp))
                }
            } else {
                Text(text = "Lancer le Scan BLE", modifier = Modifier.padding(end = 8.dp), fontSize = 22.sp)

                IconButton(onClick = { onScanClick() }) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start", tint = Color.Green, modifier = Modifier.size(32.dp))
                }
            }
        }

        // Barre de progression pendant le scan
        if (isScanning) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Liste des appareils trouvés
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(scanResults) { device ->
                BleDeviceItem(device = device)
            }
        }
    }
}

@Composable
fun BleDeviceItem(device: BleDevice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${device.rssi}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Blue)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Nom: ${device.name}")
                    Text(text = "Adresse MAC: ${device.macAddress}")
                }
            }
        }
    }
}

data class BleDevice(val name: String, val macAddress: String, val rssi: Int)

@Preview(showBackground = true)
@Composable
fun ScanScreenPreview() {
    AndroidSmartDeviceTheme {
        ScanScreen(onScanClick = {}, scanResults = emptyList(), isScanning = false, progress = 0f)
    }
}
