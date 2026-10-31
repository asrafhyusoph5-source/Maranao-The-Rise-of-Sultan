package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CouncilPolicy
import com.example.model.KingdomStats
import com.example.model.PlayerRank
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KingdomManagementScreen(
  currentRank: PlayerRank,
  kingdomStats: KingdomStats,
  onEnactPolicy: (CouncilPolicy) -> Unit,
  onBack: () -> Unit
) {
  val isLeader = currentRank.level >= 3 // Unlocked at Community Leader & above

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Kingdom & Council Simulator", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Governance of Lake Lanao Communities", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_kingdom_back")) {
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
      if (!isLeader) {
        // Preview state for early players
        Surface(
          color = RoyalCrimsonDark.copy(alpha = 0.7f),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("👑 Royal Council Preview", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              "You are currently at Rank Tier ${currentRank.level} (${currentRank.englishTitle}). Advance to Rank Tier 3 (Community Leader) or Tier 4 (Datu) to participate in governing the Four Principalities of Lanao.",
              color = TextPrimary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
          }
        }
      }

      // KINGDOM METRICS GRID
      Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Realm Statistics (Lake Lanao)", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatPill(title = "Treasury", value = "${kingdomStats.treasury} 💰", color = MaranaoGoldLight, modifier = Modifier.weight(1f))
            StatPill(title = "Population", value = "${kingdomStats.population} 👥", color = TextPrimary, modifier = Modifier.weight(1f))
            StatPill(title = "Happiness", value = "${kingdomStats.villageHappiness}% 😊", color = MindanaoGreenLight, modifier = Modifier.weight(1f))
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatPill(title = "Food Reserves", value = "${kingdomStats.foodReserves} 🌾", color = LakeLanaoShore, modifier = Modifier.weight(1f))
            StatPill(title = "Heritage", value = "${kingdomStats.culturalHeritageScore}% 🏛️", color = LangkitMagenta, modifier = Modifier.weight(1f))
            StatPill(title = "Infrastructure", value = "${kingdomStats.infrastructureLevel}% 🏗️", color = MaranaoGold, modifier = Modifier.weight(1f))
          }
        }
      }

      // ROYAL COUNCIL POLICIES & DECREES
      Text("Royal Council Decrees (Taritib)", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        kingdomStats.policies.forEach { policy ->
          PolicyCard(
            policy = policy,
            canEnact = isLeader && !policy.isEnacted && kingdomStats.treasury >= policy.goldCost,
            onEnact = { onEnactPolicy(policy) }
          )
        }
      }
    }
  }
}

@Composable
fun PolicyCard(
  policy: CouncilPolicy,
  canEnact: Boolean,
  onEnact: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = if (policy.isEnacted) SurfaceContainerDark else SurfaceCard),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (policy.isEnacted) MindanaoGreenLight else SurfaceCardBorder
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = policy.name, color = TextGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Text(text = policy.maranaoName, color = MaranaoGoldLight, fontSize = 12.sp)
        }

        if (policy.isEnacted) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Check, contentDescription = "Enacted", tint = MindanaoGreenLight, modifier = Modifier.size(16.dp))
            Text("Enacted", color = MindanaoGreenLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Text(text = policy.description, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)

      // Costs and Benefits
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Cost: ${policy.goldCost} 💰", color = MaranaoGoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (policy.heritageBoost > 0) Text("+${policy.heritageBoost}% 🏛️", color = LangkitMagenta, fontSize = 11.sp)
          if (policy.happinessBoost > 0) Text("+${policy.happinessBoost}% 😊", color = MindanaoGreenLight, fontSize = 11.sp)
          if (policy.infrastructureBoost > 0) Text("+${policy.infrastructureBoost}% 🏗️", color = MaranaoGold, fontSize = 11.sp)
        }
      }

      if (!policy.isEnacted) {
        Button(
          onClick = onEnact,
          enabled = canEnact,
          colors = ButtonDefaults.buttonColors(containerColor = RoyalCrimson, contentColor = MaranaoGoldLight),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("btn_enact_${policy.id}")
        ) {
          Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Decree Policy in Royal Council", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
