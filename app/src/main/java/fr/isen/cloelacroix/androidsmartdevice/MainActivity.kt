package fr.isen.cloelacroix.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.cloelacroix.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSmartDeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onStartScan = {
                            val intent = Intent(this, ScanActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, onStartScan: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bandeau bleu en haut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(bottom = 16.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "AndroidSmartDevice",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),

                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Message de bienvenue
        Text(
            text = "Bienvenue sur votre application Smart Device",
            color = Color(0xFF1A237E), // Bleu foncé
            fontSize = 28.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Description
        Text(
            text = "Cette application permet de scanner les appareils BLE à proximité.",
            color = Color.Gray, // Noir clair
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(42.dp))

        // Logo Bluetooth
        Image(
            painter = painterResource(id = R.drawable.logo_bluetooth),
            contentDescription = "Logo Bluetooth",
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(62.dp))

        // Bouton Commencer le Scan
        Button(
            onClick = onStartScan,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
        ) {
            Text(text = "Commencer le Scan")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AndroidSmartDeviceTheme {
        MainScreen(onStartScan = {})
    }
}
