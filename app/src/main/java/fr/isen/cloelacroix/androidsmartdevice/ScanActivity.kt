package fr.isen.cloelacroix.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.cloelacroix.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ScanActivity : ComponentActivity() {
    private val scanResults = mutableStateListOf<BleDevice>()
    private var isScanning by mutableStateOf(false)
    private var progress by mutableStateOf(0f)
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val requestBluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                startScan()
            } else {
                Toast.makeText(this, "Permissions refusées", Toast.LENGTH_SHORT).show()
            }
        }

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (isBluetoothEnabled) {
                checkLocationAndStartScan()
            } else {
                Toast.makeText(this, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScanScreen(
                        onScanClick = {
                            checkBluetoothAndLocationAndStartScan()
                        },
                        onStopScanClick = {
                            stopScan() // Arrêter le scan manuellement
                        },
                        scanResults = scanResults,
                        isScanning = isScanning,
                        progress = progress,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkBluetoothAndLocationAndStartScan() {
        if (isBluetoothEnabled) {
            checkLocationAndStartScan()
        } else {
            Toast.makeText(this, "Veuillez activer le Bluetooth", Toast.LENGTH_SHORT).show()
            enableBluetooth()
        }
    }

    private fun checkLocationAndStartScan() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isLocationEnabled) {
            checkBluetoothPermissionsAndStartScan()
        } else {
            Toast.makeText(this, "Veuillez activer la localisation", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkBluetoothPermissionsAndStartScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            val missingPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (missingPermissions.isNotEmpty()) {
                requestBluetoothPermissionLauncher.launch(missingPermissions)
            } else {
                startScan()
            }
        } else {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            val missingPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (missingPermissions.isNotEmpty()) {
                requestBluetoothPermissionLauncher.launch(missingPermissions)
            } else {
                startScan()
            }
        }
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter != null) {
            if (!isBluetoothEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }

    private val scanTimeout: Long = 20000 // Durée maximale du scan en millisecondes (10 secondes)
    private var scanHandler: Handler? = null

    private fun startScan() {
        isScanning = true
        progress = 0f
        scanResults.clear()
        bluetoothLeScanner?.startScan(scanCallback)

        // Arrêter le scan après un délai de `scanTimeout`
        scanHandler = Handler()
        scanHandler?.postDelayed({
            stopScan()
        }, scanTimeout)

        simulateProgress()
    }

    private fun stopScan() {
        isScanning = false
        bluetoothLeScanner?.stopScan(scanCallback)
        scanHandler?.removeCallbacksAndMessages(null)
    }

    private fun simulateProgress() {
        val handler = Handler()
        for (i in 1..200) {
            handler.postDelayed({
                progress = i / 200f
                if (i == 200) stopScan()
            }, (i * 200).toLong())
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val rssi = result.rssi
            val name = device.name ?: "Device Unknown"

            // Filtrer les périphériques sans nom
            if (name != "Device Unknown") {
                val macAddress = device.address

                // Ajouter seulement si l'appareil n'est pas déjà dans la liste
                val bleDevice = BleDevice(name, macAddress, rssi)
                if (scanResults.find { it.macAddress == macAddress } == null) {
                    scanResults.add(bleDevice)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(this@ScanActivity, "Scan failed: $errorCode", Toast.LENGTH_SHORT).show()
            stopScan()
        }
    }

}

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

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Device Name: ${device.deviceName}")
                    Text(text = "MAC Address: ${device.macAddress}")
                }
            }
        }
    }
}

data class BleDevice(val deviceName: String, val macAddress: String, val rssi: Int)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidSmartDeviceTheme {
        ScanScreen(
            onScanClick = {},
            scanResults = listOf(
                BleDevice("Device 1", "00:11:22:33:44:55", -60),
                BleDevice("Device 2", "00:11:22:33:44:66", -50),
                BleDevice("Device 3", "00:11:22:33:44:77", -40)
            ),
            isScanning = false,
            progress = 0f,
            onStopScanClick = TODO(),
            modifier = TODO()
        )
    }
}
