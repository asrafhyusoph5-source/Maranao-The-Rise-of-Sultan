package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerCustomization
import com.example.model.PlayerRank
import com.example.model.PlayerStats
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
  playerCustomization: PlayerCustomization,
  playerStats: PlayerStats,
  currentRank: PlayerRank,
  onBack: () -> Unit
) {
  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Player Profile", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Lineage & Deeds of ${playerCustomization.name}", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_profile_back")) {
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
      // REGAL RANK BANNER
      Surface(
        color = RoyalCrimsonDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaranaoGold),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .background(SurfaceDark, CircleShape)
              .border(2.dp, MaranaoGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WorkspacePremium,
              contentDescription = "Rank Emblem",
              tint = MaranaoGold,
              modifier = Modifier.size(40.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = currentRank.maranaoTitle,
            color = TextGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = currentRank.englishTitle,
            color = MaranaoGoldLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Rank Tier ${currentRank.level} of 6",
            color = TextSecondary,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = currentRank.description,
            color = TextPrimary.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
        }
      }

      // PROGRESSION TO SULTAN ROADMAP
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Path to the Sultanate", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)

          val nextRank = PlayerRank.values().getOrNull(currentRank.ordinal + 1)
          if (nextRank != null) {
            Text(
              "Next Rank: ${nextRank.maranaoTitle} (${nextRank.englishTitle})",
              color = TextPrimary,
              fontSize = 13.sp
            )

            // Reputation progress
            val repProgress = (playerStats.reputation.toFloat() / nextRank.requiredReputation).coerceIn(0f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Reputation", color = TextSecondary, fontSize = 11.sp)
                Text("${playerStats.reputation} / ${nextRank.requiredReputation}", color = MaranaoGoldLight, fontSize = 11.sp)
              }
              LinearProgressIndicator(
                progress = { repProgress },
                color = MaranaoGold,
                trackColor = SurfaceDark,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
              )
            }

            // Trust progress
            val trustProgress = (playerStats.communityTrust.toFloat() / nextRank.requiredTrust).coerceIn(0f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Community Trust", color = TextSecondary, fontSize = 11.sp)
                Text("${playerStats.communityTrust}% / ${nextRank.requiredTrust}%", color = MindanaoGreenLight, fontSize = 11.sp)
              }
              LinearProgressIndicator(
                progress = { trustProgress },
                color = MindanaoGreenLight,
                trackColor = SurfaceDark,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
              )
            }
          } else {
            Text(
              "✨ You have attained the supreme rank of SULTAN KO RANAO. You hold the sacred trust of all Lake Lanao.",
              color = TextGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // STATS GRID
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Character Attributes", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatPill(
              title = "Dirhams",
              value = "${playerStats.gold} 💰",
              color = MaranaoGoldLight,
              modifier = Modifier.weight(1f)
            )
            StatPill(
              title = "Trust",
              value = "${playerStats.communityTrust}% 🤝",
              color = MindanaoGreenLight,
              modifier = Modifier.weight(1f)
            )
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatPill(
              title = "Wisdom",
              value = "${playerStats.wisdom} 📜",
              color = LakeLanaoShore,
              modifier = Modifier.weight(1f)
            )
            StatPill(
              title = "Culture Lore",
              value = "${playerStats.culturalKnowledge} 🏛️",
              color = LangkitMagenta,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // ATTIRE & WARDROBE DETAILS
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Attire & Regalia", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text("• Headwear: ${playerCustomization.headwear}", color = TextPrimary, fontSize = 13.sp)
          Text("• Upper Garment: ${playerCustomization.upperClothing}", color = TextPrimary, fontSize = 13.sp)
          Text("• Malong: ${playerCustomization.lowerClothing}", color = TextPrimary, fontSize = 13.sp)
          Text("• Accessory: ${playerCustomization.accessory}", color = TextPrimary, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
fun StatPill(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    color = SurfaceDark,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, color = TextSecondary, fontSize = 11.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
  }
}
