package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PlayerCustomization
import com.example.model.PlayerRank
import com.example.ui.theme.*

@Composable
fun MainMenuScreen(
  onStartGame: () -> Unit,
  onContinueGame: () -> Unit,
  hasSaveData: Boolean,
  onCustomizeCharacter: () -> Unit,
  onOpenLibrary: () -> Unit,
  onOpenSettings: () -> Unit,
  playerRank: PlayerRank,
  playerCustomization: PlayerCustomization
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceDark)
  ) {
    // HERO BANNER ARTWORK
    Image(
      painter = painterResource(id = R.drawable.img_hero_lake_lanao),
      contentDescription = "Lake Lanao at sunset with Torogan palaces",
      modifier = Modifier
        .fillMaxWidth()
        .height(340.dp),
      contentScale = ContentScale.Crop
    )

    // Dark Gradient overlay to blend hero art into UI
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(340.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              SurfaceDark.copy(alpha = 0.5f),
              SurfaceDark
            ),
            startY = 100f,
            endY = 850f
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 130.dp)
      ) {
        // Sarimanok / Torogan emblem icon
        Surface(
          color = RoyalCrimson,
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(2.dp, MaranaoGold),
          modifier = Modifier.size(64.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = MaranaoGold,
              modifier = Modifier.size(36.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "MARANAO",
          color = TextGold,
          fontSize = 32.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 4.sp
        )

        Text(
          text = "RISE OF THE SULTAN",
          color = MaranaoGoldLight,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp
        )

        Text(
          text = "The Legacy of Lake Lanao",
          color = TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Cultural proverb
        Surface(
          color = SurfaceContainerDark.copy(alpha = 0.9f),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "“Kadatuan is not power over others, but guardianship of our heritage, customary order (Taritib), and service to our people under Allah.”",
            color = TextPrimary.copy(alpha = 0.9f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // MAIN MENU ACTION BUTTONS
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Play / Enter World
        Button(
          onClick = onStartGame,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaranaoGold,
            contentColor = ToroganWoodDark
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("btn_menu_play")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (hasSaveData) "Continue as ${playerCustomization.name} (${playerRank.maranaoTitle})" else "Enter Lake Lanao",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // New Game / Character Creation
        OutlinedButton(
          onClick = onCustomizeCharacter,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGold),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_menu_create_character")
        ) {
          Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Character Customizer & Attire", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        // Cultural Lore & Heritage Library
        OutlinedButton(
          onClick = onOpenLibrary,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
          border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_menu_cultural_library")
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaranaoGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Lake Lanao Heritage Archive", fontSize = 14.sp)
        }

        // Settings
        OutlinedButton(
          onClick = onOpenSettings,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
          border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_menu_settings")
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Settings & Sound", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
