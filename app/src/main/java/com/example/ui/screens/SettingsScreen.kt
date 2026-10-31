package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  graphicsQuality: String,
  onGraphicsQualityChange: (String) -> Unit,
  audioEnabled: Boolean,
  onAudioToggle: (Boolean) -> Unit,
  cameraSensitivity: Float,
  onSensitivityChange: (Float) -> Unit,
  onSaveGame: () -> Unit,
  onLoadGame: () -> Unit,
  saveMessage: String?,
  onBack: () -> Unit
) {
  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Game Settings & Chronicles", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Configuration & Save Data", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_settings_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextGold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = SurfaceContainerDark,
          titleContentColor = TextGold
        )
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      if (saveMessage != null) {
        Surface(
          color = RoyalCrimsonDark,
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = saveMessage,
            color = TextGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // SAVE & LOAD SYSTEM
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Persistent Chronicle (Save & Load)", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text(
            "Your progress across Lake Lanao is preserved in local storage including character rank, inventory, and kingdom state.",
            color = TextPrimary,
            fontSize = 12.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
              onClick = onSaveGame,
              colors = ButtonDefaults.buttonColors(containerColor = MaranaoGold, contentColor = ToroganWoodDark),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).testTag("btn_save_game")
            ) {
              Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Save Game", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = onLoadGame,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGold),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).testTag("btn_load_game")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Load Game", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }

      // GRAPHICS QUALITY
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Graphics Quality", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Low (Performance)", "Medium", "High (Vivid)").forEach { q ->
              FilterChip(
                selected = graphicsQuality.startsWith(q.take(3)),
                onClick = { onGraphicsQualityChange(q) },
                label = { Text(q.take(7)) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalCrimson,
                  selectedLabelColor = MaranaoGoldLight
                ),
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }

      // AUDIO & AMBIENCE
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Lake Lanao Soundscape", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text("Lake waves, birds, and harmonic kulintang chimes", color = TextSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = audioEnabled,
              onCheckedChange = onAudioToggle,
              colors = SwitchDefaults.colors(
                checkedThumbColor = MaranaoGold,
                checkedTrackColor = RoyalCrimson
              )
            )
          }
        }
      }

      // CAMERA SENSITIVITY
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Camera Orbit Sensitivity: ${(cameraSensitivity * 100).toInt()}%", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Slider(
            value = cameraSensitivity,
            onValueChange = onSensitivityChange,
            valueRange = 0.2f..1.8f,
            colors = SliderDefaults.colors(thumbColor = MaranaoGold, activeTrackColor = RoyalCrimson)
          )
        }
      }

      // CULTURAL HERITAGE ATTRIBUTION
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("Cultural Heritage & Preservation", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(
            "MARANAO: RISE OF THE SULTAN is created to celebrate the timeless heritage, Torogan architecture, Okir artistry, and community leadership of the Maranao people of Lake Lanao, Mindanao, Philippines.",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}
