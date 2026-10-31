package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gender
import com.example.model.PlayerCustomization
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
  initialCustomization: PlayerCustomization,
  onCompleteCreation: (PlayerCustomization) -> Unit,
  onBackToMenu: () -> Unit
) {
  var name by remember { mutableStateOf(initialCustomization.name) }
  var gender by remember { mutableStateOf(initialCustomization.gender) }
  var skinIndex by remember { mutableStateOf(initialCustomization.skinToneIndex) }
  var headwear by remember { mutableStateOf(initialCustomization.headwear) }
  var upperClothing by remember { mutableStateOf(initialCustomization.upperClothing) }
  var lowerClothing by remember { mutableStateOf(initialCustomization.lowerClothing) }
  var accessory by remember { mutableStateOf(initialCustomization.accessory) }

  val skinColors = listOf(
    Color(0xFFD2996E),
    Color(0xFFBA7A45),
    Color(0xFFA06332),
    Color(0xFF7F4B23)
  )

  val headwearOptions = if (gender == Gender.MALE) {
    listOf("Tubao Headband", "Embroidered Songkok", "Royal Turban", "None")
  } else {
    listOf("Traditional Kombong", "Silk Hijab", "Beaded Diadem", "None")
  }

  val upperOptions = listOf(
    "Embroidered Bating Vest",
    "Woven Silk Barong",
    "Ceremonial Royal Tunic",
    "Lakeside Artisan Shirt"
  )

  val lowerOptions = listOf(
    "Malong Landap (Yellow & Red)",
    "Malong Andon (Geometric)",
    "Traditional Silk Trousers",
    "Royal Emerald Malong"
  )

  val accessoryOptions = listOf(
    "Brass Betel Box",
    "Ceremonial Kris Sash",
    "Lanao Pearl Brooch",
    "None"
  )

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Character Creation", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Create your legacy in Lake Lanao", color = TextSecondary, fontSize = 12.sp)
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
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Spacer(modifier = Modifier.height(6.dp))

      // Character Name Input
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Character Name", color = TextSecondary) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedBorderColor = MaranaoGold,
          unfocusedBorderColor = SurfaceCardBorder,
          focusedContainerColor = SurfaceCard,
          unfocusedContainerColor = SurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_character_name")
      )

      // Gender Selection
      Text("Identity & Lineage", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        FilterChip(
          selected = gender == Gender.MALE,
          onClick = {
            gender = Gender.MALE
            if (headwear !in headwearOptions) headwear = "Tubao Headband"
          },
          label = { Text("Male (Kanakan)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalCrimson,
            selectedLabelColor = MaranaoGoldLight
          ),
          modifier = Modifier.weight(1f).testTag("chip_gender_male")
        )
        FilterChip(
          selected = gender == Gender.FEMALE,
          onClick = {
            gender = Gender.FEMALE
            headwear = "Traditional Kombong"
          },
          label = { Text("Female (Raga)") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalCrimson,
            selectedLabelColor = MaranaoGoldLight
          ),
          modifier = Modifier.weight(1f).testTag("chip_gender_female")
        )
      }

      // Skin Tone Selector
      Text("Complexion", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        skinColors.forEachIndexed { index, color ->
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(color, CircleShape)
              .border(
                width = if (skinIndex == index) 3.dp else 1.dp,
                color = if (skinIndex == index) MaranaoGold else SurfaceCardBorder,
                shape = CircleShape
              )
              .clickable { skinIndex = index }
              .testTag("skin_tone_$index"),
            contentAlignment = Alignment.Center
          ) {
            if (skinIndex == index) {
              Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.White)
            }
          }
        }
      }

      // Headwear Selection
      Text("Traditional Headwear", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        headwearOptions.forEach { opt ->
          Surface(
            color = if (headwear == opt) RoyalCrimsonDark else SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (headwear == opt) MaranaoGold else SurfaceCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { headwear = opt }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(opt, color = if (headwear == opt) TextGold else TextPrimary, fontSize = 14.sp)
              if (headwear == opt) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaranaoGold)
              }
            }
          }
        }
      }

      // Upper Garment Selection
      Text("Upper Garment", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        upperOptions.forEach { opt ->
          Surface(
            color = if (upperClothing == opt) RoyalCrimsonDark else SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (upperClothing == opt) MaranaoGold else SurfaceCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { upperClothing = opt }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(opt, color = if (upperClothing == opt) TextGold else TextPrimary, fontSize = 14.sp)
              if (upperClothing == opt) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaranaoGold)
              }
            }
          }
        }
      }

      // Lower Garment Selection (Malong Landap)
      Text("Lower Garment (Malong)", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lowerOptions.forEach { opt ->
          Surface(
            color = if (lowerClothing == opt) RoyalCrimsonDark else SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (lowerClothing == opt) MaranaoGold else SurfaceCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { lowerClothing = opt }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(opt, color = if (lowerClothing == opt) TextGold else TextPrimary, fontSize = 14.sp)
              if (lowerClothing == opt) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaranaoGold)
              }
            }
          }
        }
      }

      // Accessories
      Text("Heirloom Accessory", color = TextGold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        accessoryOptions.forEach { opt ->
          Surface(
            color = if (accessory == opt) RoyalCrimsonDark else SurfaceCard,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (accessory == opt) MaranaoGold else SurfaceCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { accessory = opt }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(opt, color = if (accessory == opt) TextGold else TextPrimary, fontSize = 14.sp)
              if (accessory == opt) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaranaoGold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Complete Creation Button
      Button(
        onClick = {
          val custom = PlayerCustomization(
            gender = gender,
            name = if (name.isBlank()) "Al-Rashid" else name,
            skinToneIndex = skinIndex,
            headwear = headwear,
            upperClothing = upperClothing,
            lowerClothing = lowerClothing,
            accessory = accessory
          )
          onCompleteCreation(custom)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaranaoGold,
          contentColor = ToroganWoodDark
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_confirm_character")
      ) {
        Text(
          text = "Begin Journey in Lake Lanao",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
