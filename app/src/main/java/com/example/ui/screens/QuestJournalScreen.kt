package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quest
import com.example.model.QuestStatus
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestJournalScreen(
  quests: List<Quest>,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Active, 1: Completed, 2: All

  val filteredQuests = when (selectedTab) {
    0 -> quests.filter { it.status == QuestStatus.IN_PROGRESS || it.status == QuestStatus.READY_TO_TURN_IN }
    1 -> quests.filter { it.status == QuestStatus.COMPLETED }
    else -> quests
  }

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Quest Journal", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Annals of Lake Lanao", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_journal_back")) {
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
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Tab selector
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = SurfaceCard,
        contentColor = MaranaoGold,
        modifier = Modifier.border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Active (${quests.count { it.status == QuestStatus.IN_PROGRESS }})") }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Completed (${quests.count { it.status == QuestStatus.COMPLETED }})") }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("All (${quests.size})") }
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        items(filteredQuests) { quest ->
          QuestCard(quest = quest)
        }
      }
    }
  }
}

@Composable
fun QuestCard(quest: Quest) {
  val isComplete = quest.status == QuestStatus.COMPLETED

  Card(
    colors = CardDefaults.cardColors(containerColor = if (isComplete) SurfaceDark else SurfaceCard),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isComplete) MindanaoGreenLight.copy(alpha = 0.5f) else MaranaoGold.copy(alpha = 0.6f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Chapter ${quest.chapter} • ${quest.giverName}",
            color = MaranaoGoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = quest.title,
            color = TextGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = quest.maranaoTitle,
            color = TextSecondary,
            fontSize = 12.sp
          )
        }

        Icon(
          imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.Pending,
          contentDescription = null,
          tint = if (isComplete) MindanaoGreenLight else MaranaoGold,
          modifier = Modifier.size(24.dp)
        )
      }

      Text(
        text = quest.summary,
        color = TextPrimary,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      // Cultural Lore Callout
      Surface(
        color = SurfaceContainerDark,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text("Cultural Wisdom:", color = MaranaoGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text(quest.culturalSignificance, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
        }
      }

      // Objectives List
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Objectives:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        quest.objectives.forEach { obj ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (obj.isFinished) Icons.Default.CheckCircle else Icons.Default.Pending,
              contentDescription = null,
              tint = if (obj.isFinished) MindanaoGreenLight else TextSecondary,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${obj.description} (${obj.currentCount}/${obj.requiredCount})",
              color = if (obj.isFinished) TextSecondary else TextPrimary,
              fontSize = 12.sp
            )
          }
        }
      }

      // Rewards line
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Rewards: +${quest.goldReward} 💰", color = MaranaoGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("+${quest.trustReward}% Trust 🤝", color = MindanaoGreenLight, fontSize = 11.sp)
        Text("+${quest.wisdomReward} Wisdom 📜", color = LakeLanaoShore, fontSize = 11.sp)
      }
    }
  }
}
