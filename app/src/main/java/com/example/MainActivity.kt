package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = SurfaceDark
        ) {
          MaranaoApp()
        }
      }
    }
  }
}

@Composable
fun MaranaoApp(viewModel: GameViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsState()

  // Handle system back button per screen
  if (uiState.currentScreen != ScreenDestination.MAIN_MENU) {
    BackHandler {
      when (uiState.currentScreen) {
        ScreenDestination.OPEN_WORLD_GAME -> viewModel.navigateTo(ScreenDestination.MAIN_MENU)
        ScreenDestination.CHARACTER_CREATION -> viewModel.navigateTo(ScreenDestination.MAIN_MENU)
        ScreenDestination.CULTURAL_LIBRARY -> viewModel.navigateTo(ScreenDestination.MAIN_MENU)
        ScreenDestination.SETTINGS -> viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME)
        else -> viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME)
      }
    }
  }

  when (uiState.currentScreen) {
    ScreenDestination.MAIN_MENU -> {
      MainMenuScreen(
        onStartGame = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) },
        onContinueGame = {
          viewModel.triggerLoad()
          viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME)
        },
        hasSaveData = true,
        onCustomizeCharacter = { viewModel.navigateTo(ScreenDestination.CHARACTER_CREATION) },
        onOpenLibrary = { viewModel.navigateTo(ScreenDestination.CULTURAL_LIBRARY) },
        onOpenSettings = { viewModel.navigateTo(ScreenDestination.SETTINGS) },
        playerRank = uiState.playerRank,
        playerCustomization = uiState.playerCustomization
      )
    }

    ScreenDestination.CHARACTER_CREATION -> {
      CharacterCreationScreen(
        initialCustomization = uiState.playerCustomization,
        onCompleteCreation = { newCustomization ->
          viewModel.setPlayerCustomization(newCustomization)
        },
        onBackToMenu = { viewModel.navigateTo(ScreenDestination.MAIN_MENU) }
      )
    }

    ScreenDestination.OPEN_WORLD_GAME -> {
      OpenWorldScreen(
        viewModel = viewModel,
        uiState = uiState
      )
    }

    ScreenDestination.PLAYER_PROFILE -> {
      PlayerProfileScreen(
        playerCustomization = uiState.playerCustomization,
        playerStats = uiState.playerStats,
        currentRank = uiState.playerRank,
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.QUEST_JOURNAL -> {
      QuestJournalScreen(
        quests = uiState.quests,
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.MARKETPLACE -> {
      MarketplaceScreen(
        playerGold = uiState.playerStats.gold,
        inventoryItemIds = uiState.inventoryItemIds,
        onBuyItem = { item -> viewModel.buyItem(item) },
        onSellItem = { item -> viewModel.sellItem(item) },
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.WORLD_MAP -> {
      WorldMapScreen(
        playerPos = viewModel.worldEngine.playerPosition,
        onFastTravelTo = { targetPos -> viewModel.fastTravelTo(targetPos) },
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.KINGDOM_MANAGEMENT -> {
      KingdomManagementScreen(
        currentRank = uiState.playerRank,
        kingdomStats = uiState.kingdomStats,
        onEnactPolicy = { policy -> viewModel.enactCouncilPolicy(policy) },
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.SETTINGS -> {
      SettingsScreen(
        graphicsQuality = uiState.graphicsQuality,
        onGraphicsQualityChange = { q -> viewModel.setGraphicsQuality(q) },
        audioEnabled = uiState.audioEnabled,
        onAudioToggle = { a -> viewModel.toggleAudio(a) },
        cameraSensitivity = uiState.cameraSensitivity,
        onSensitivityChange = { s -> viewModel.setSensitivity(s) },
        onSaveGame = { viewModel.triggerSave() },
        onLoadGame = { viewModel.triggerLoad() },
        saveMessage = uiState.saveToastMessage,
        onBack = { viewModel.navigateTo(ScreenDestination.OPEN_WORLD_GAME) }
      )
    }

    ScreenDestination.CULTURAL_LIBRARY -> {
      CulturalLibraryScreen(
        onBack = { viewModel.navigateTo(ScreenDestination.MAIN_MENU) }
      )
    }
  }
}
