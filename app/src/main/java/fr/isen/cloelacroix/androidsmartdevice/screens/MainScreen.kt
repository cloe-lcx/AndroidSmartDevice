package fr.isen.cloelacroix.androidsmartdevice.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.cloelacroix.androidsmartdevice.R

@Composable
fun MainScreen(modifier: Modifier = Modifier, onStartScan: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Bienvenue sur votre application Smart Device",
            color = Color(0xFFE61F93),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "Cette application permet de scanner les appareils BLE à proximité.",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_bluetooth),
            contentDescription = "Logo Bluetooth",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartScan,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE61F93))
        ) {
            Text(
                text = "Commencer le Scan",
                color = Color.White
            )
        }
    }
}