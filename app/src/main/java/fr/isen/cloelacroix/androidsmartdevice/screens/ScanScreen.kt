package fr.isen.cloelacroix.androidsmartdevice.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.cloelacroix.androidsmartdevice.BleDevice
import fr.isen.cloelacroix.androidsmartdevice.BleDeviceItem

@Composable
fun ScanScreen(
    onScanClick: () -> Unit,
    onStopScanClick: () -> Unit,  // Ajouter une fonction pour arrêter le scan manuellement
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

                // Bouton pour arrêter le scan manuellement
                IconButton(onClick = { onStopScanClick() }) {
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
