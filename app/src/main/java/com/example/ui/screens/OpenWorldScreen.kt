package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.GameCanvasView
import com.example.ui.components.NpcDialogueDialog
import com.example.ui.components.TouchControlsOverlay
import com.example.ui.theme.*
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun OpenWorldScreen(
  viewModel: GameViewModel,
  uiState: GameUiState
) {
  BackHandler {
    viewModel.navigateTo(ScreenDestination.MAIN_MENU)
  }

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. 3D GAMEPLAY CANVAS
    GameCanvasView(
      worldEngine = viewModel.worldEngine,
      playerCustomization = uiState.playerCustomization,
      onInteractWith = { /* Proximity interactable detected */ },
      modifier = Modifier.fillMaxSize()
    )

    // 2. TOUCH CONTROLS & HUD OVERLAY
    TouchControlsOverlay(
      onJoystickMove = { x, y -> viewModel.updateJoystick(x, y) },
      onRunToggle = { running -> viewModel.toggleRun(running) },
      isRunning = uiState.isRunning,
      interactableTarget = uiState.nearestTarget,
      onInteractClick = { viewModel.handleInteractClick() },
      onOpenJournal = { viewModel.navigateTo(ScreenDestination.QUEST_JOURNAL) },
      onOpenInventory = { viewModel.navigateTo(ScreenDestination.MARKETPLACE) },
      onOpenMap = { viewModel.navigateTo(ScreenDestination.WORLD_MAP) },
      onOpenKingdom = { viewModel.navigateTo(ScreenDestination.KINGDOM_MANAGEMENT) },
      onOpenSettings = { viewModel.navigateTo(ScreenDestination.SETTINGS) },
      playerRankTitle = "${uiState.playerRank.maranaoTitle} (${uiState.playerCustomization.name})",
      goldAmount = uiState.playerStats.gold,
      trustPercent = uiState.playerStats.communityTrust
    )

    // 3. NPC DIALOGUE DIALOG (When interacting)
    if (uiState.activeDialogueNpc != null) {
      NpcDialogueDialog(
        npc = uiState.activeDialogueNpc,
        onDismiss = { viewModel.dismissDialogue() },
        onSelectChoice = { choice -> viewModel.selectDialogueChoice(choice) },
        onOpenShop = {
          viewModel.dismissDialogue()
          viewModel.navigateTo(ScreenDestination.MARKETPLACE)
        }
      )
    }

    // 4. BUILDING LORE DIALOG
    if (uiState.activeBuildingLore != null) {
      Dialog(onDismissRequest = { viewModel.dismissDialogue() }) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = SurfaceDark,
          border = androidx.compose.foundation.BorderStroke(2.dp, MaranaoGold),
          modifier = Modifier.padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Info, contentDescription = null, tint = MaranaoGold)
              Text("Ancestral Architecture", color = TextGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(
              text = uiState.activeBuildingLore,
              color = TextPrimary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
            Button(
              onClick = { viewModel.dismissDialogue() },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalCrimson, contentColor = MaranaoGoldLight),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("btn_close_building_lore")
            ) {
              Text("Close", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 5. SAVE / EVENT NOTIFICATION TOAST
    if (uiState.saveToastMessage != null) {
      Surface(
        color = SurfaceDark.copy(alpha = 0.95f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 70.dp, start = 24.dp, end = 24.dp)
      ) {
        Text(
          text = uiState.saveToastMessage,
          color = TextGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }
    }
  }
}
