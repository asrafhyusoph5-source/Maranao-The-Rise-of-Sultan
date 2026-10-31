package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.Vector3
import com.example.ui.theme.*

data class MapPin(
  val id: String,
  val name: String,
  val maranaoName: String,
  val description: String,
  val worldPos: Vector3,
  val iconEmoji: String,
  val pinColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldMapScreen(
  playerPos: Vector3,
  onFastTravelTo: (Vector3) -> Unit,
  onBack: () -> Unit
) {
  val mapPins = listOf(
    MapPin(
      id = "pin_torogan",
      name = "Ancestral Torogan Palace",
      maranaoName = "Kialdan a Torogan",
      description = "Heart of royal governance and community assemblies, flanked by soaring Panolong wing beams.",
      worldPos = Vector3(0f, 0f, 10f),
      iconEmoji = "🏛️",
      pinColor = RoyalCrimson
    ),
    MapPin(
      id = "pin_weaving",
      name = "Lakeside Weaving Pavilion",
      maranaoName = "Darpa ko Langkit",
      description = "Where master weaver Bae Fatima weaves the prestigious Malong Landap.",
      worldPos = Vector3(-12f, 0f, 4f),
      iconEmoji = "🧵",
      pinColor = MindanaoGreenLight
    ),
    MapPin(
      id = "pin_forge",
      name = "Master Brass Forge & Boatyard",
      maranaoName = "Pandayan ko Tombaga",
      description = "Panday Bilal casts resonance-tuned Kulintang gongs and shapes Awang canoes.",
      worldPos = Vector3(12f, 0f, -2f),
      iconEmoji = "🔨",
      pinColor = MaranaoGold
    ),
    MapPin(
      id = "pin_dock",
      name = "Lake Lanao Shore Docks",
      maranaoName = "Pantalan ko Ranao",
      description = "Fishing pier where Awang boats depart for the deep waters of Lake Lanao.",
      worldPos = Vector3(0f, 0f, -12f),
      iconEmoji = "⛵",
      pinColor = LakeLanaoShore
    ),
    MapPin(
      id = "pin_market",
      name = "Community Bazaar & Market",
      maranaoName = "Padang ko Padian",
      description = "Bustling marketplace with freshwater pearls, sweet dodol, and woven tapestries.",
      worldPos = Vector3(-5f, 0f, 0f),
      iconEmoji = "🧺",
      pinColor = LangkitOrange
    ),
    MapPin(
      id = "pin_mosque",
      name = "Peaceful Mosque Courtyard",
      maranaoName = "Masjid ko Ingud",
      description = "Sanctuary of peace, prayer reflection, and Quranic study.",
      worldPos = Vector3(0f, 0f, 26f),
      iconEmoji = "🕌",
      pinColor = MindanaoGreen
    )
  )

  var selectedPin by remember { mutableStateOf(mapPins[0]) }

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Regional Map of Lake Lanao", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Pat a Pangampong ko Ranao", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_map_back")) {
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
      // INTERACTIVE CANVAS MAP
      Surface(
        color = Color(0xFF1B2B1E),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaranaoGold),
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Lake Lanao water body (Bottom half)
          val lakePath = Path().apply {
            moveTo(0f, h * 0.55f)
            quadraticTo(w * 0.35f, h * 0.50f, w * 0.65f, h * 0.60f)
            lineTo(w, h * 0.58f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
          }
          drawPath(lakePath, color = LakeLanaoBlue)

          // Village road pathways
          drawLine(
            color = Color(0xFF6B5842),
            start = Offset(w * 0.5f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.65f),
            strokeWidth = 8f
          )
          drawLine(
            color = Color(0xFF5E4D3B),
            start = Offset(w * 0.22f, h * 0.40f),
            end = Offset(w * 0.78f, h * 0.42f),
            strokeWidth = 6f
          )

          // Draw Location Pins on Map
          for (pin in mapPins) {
            // Map world (-25 to 25 X, -20 to 35 Z) into Canvas (W, H)
            val px = ((pin.worldPos.x + 25f) / 50f) * w
            val py = ((35f - pin.worldPos.z) / 55f) * h

            val isSelected = selectedPin.id == pin.id

            drawCircle(
              color = pin.pinColor,
              radius = if (isSelected) 14f else 9f,
              center = Offset(px, py)
            )
            drawCircle(
              color = Color.White,
              radius = if (isSelected) 6f else 4f,
              center = Offset(px, py)
            )
          }

          // Player current GPS Position (Golden pulsing beacon)
          val playerPx = ((playerPos.x + 25f) / 50f).coerceIn(0.05f, 0.95f) * w
          val playerPy = ((35f - playerPos.z) / 55f).coerceIn(0.05f, 0.95f) * h

          drawCircle(
            color = MaranaoGold.copy(alpha = 0.4f),
            radius = 18f,
            center = Offset(playerPx, playerPy)
          )
          drawCircle(
            color = MaranaoGoldLight,
            radius = 8f,
            center = Offset(playerPx, playerPy)
          )
        }
      }

      // SELECTED LANDMARK DETAILS
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(selectedPin.iconEmoji, fontSize = 28.sp)
              Column {
                Text(selectedPin.name, color = TextGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(selectedPin.maranaoName, color = MaranaoGoldLight, fontSize = 12.sp)
              }
            }
          }

          Text(
            text = selectedPin.description,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )

          Button(
            onClick = {
              onFastTravelTo(selectedPin.worldPos)
              onBack()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaranaoGold,
              contentColor = ToroganWoodDark
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_fast_travel")
          ) {
            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Travel to ${selectedPin.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      // LIST OF ALL VILLAGE LANDMARKS
      Text("Discovered Landmarks", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        mapPins.forEach { pin ->
          Surface(
            color = if (selectedPin.id == pin.id) RoyalCrimsonDark else SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (selectedPin.id == pin.id) MaranaoGold else SurfaceCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedPin = pin }
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(pin.iconEmoji, fontSize = 20.sp)
              Column(modifier = Modifier.weight(1f)) {
                Text(pin.name, color = if (selectedPin.id == pin.id) TextGold else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(pin.maranaoName, color = TextSecondary, fontSize = 11.sp)
              }
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = pin.pinColor, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}
