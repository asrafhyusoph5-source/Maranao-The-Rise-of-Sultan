package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameSaveManager
import com.example.engine.InteractableTarget
import com.example.engine.Vector3
import com.example.engine.WorldEngine
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ScreenDestination {
  MAIN_MENU,
  CHARACTER_CREATION,
  OPEN_WORLD_GAME,
  PLAYER_PROFILE,
  QUEST_JOURNAL,
  MARKETPLACE,
  WORLD_MAP,
  KINGDOM_MANAGEMENT,
  SETTINGS,
  CULTURAL_LIBRARY
}

data class GameUiState(
  val currentScreen: ScreenDestination = ScreenDestination.MAIN_MENU,
  val playerCustomization: PlayerCustomization = PlayerCustomization(),
  val playerStats: PlayerStats = PlayerStats(),
  val playerRank: PlayerRank = PlayerRank.ORDINARY_MEMBER,
  val inventoryItemIds: List<String> = listOf("salapa_box", "dried_lake_fish", "dodol_sweet"),
  val quests: List<Quest> = QuestDatabase.initialQuests,
  val kingdomStats: KingdomStats = KingdomStats(),
  val activeDialogueNpc: NPC? = null,
  val activeBuildingLore: String? = null,
  val nearestTarget: InteractableTarget? = null,
  val joystickInput: Pair<Float, Float> = Pair(0f, 0f),
  val isRunning: Boolean = false,
  val saveToastMessage: String? = null,
  val audioEnabled: Boolean = true,
  val graphicsQuality: String = "High (Vivid)",
  val cameraSensitivity: Float = 1.0f
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
  val worldEngine: WorldEngine = WorldEngine()
  private val saveManager: GameSaveManager = GameSaveManager(application)

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  init {
    // Check if previous save exists
    if (saveManager.hasSavedGame()) {
      val loaded = saveManager.loadGame()
      if (loaded != null) {
        _uiState.update {
          it.copy(
            playerCustomization = loaded.customization,
            playerStats = loaded.stats,
            playerRank = loaded.rank,
            inventoryItemIds = loaded.inventory,
            kingdomStats = loaded.kingdom
          )
        }
      }
    }

    // Launch continuous 60FPS Game Loop ticker
    viewModelScope.launch {
      var lastTimeMillis = System.currentTimeMillis()
      while (isActive) {
        val currentTime = System.currentTimeMillis()
        val dt = ((currentTime - lastTimeMillis) / 1000f).coerceIn(0.005f, 0.05f)
        lastTimeMillis = currentTime

        if (_uiState.value.currentScreen == ScreenDestination.OPEN_WORLD_GAME) {
          val (jx, jy) = _uiState.value.joystickInput
          worldEngine.isRunning = _uiState.value.isRunning
          worldEngine.updateMovement(jx, jy, dt)

          // Check proximity to interactables
          val nearest = worldEngine.findNearestInteractable()
          _uiState.update { it.copy(nearestTarget = nearest) }

          // Auto-update objective 1 of intro quest if near Torogan or Lake
          checkAutoObjectives(worldEngine.playerPosition)
        }

        delay(16) // ~60fps
      }
    }
  }

  fun navigateTo(dest: ScreenDestination) {
    _uiState.update { it.copy(currentScreen = dest) }
  }

  fun updateJoystick(x: Float, y: Float) {
    _uiState.update { it.copy(joystickInput = Pair(x, y)) }
  }

  fun toggleRun(running: Boolean) {
    _uiState.update { it.copy(isRunning = running) }
    worldEngine.isRunning = running
  }

  fun setPlayerCustomization(customization: PlayerCustomization) {
    _uiState.update {
      it.copy(
        playerCustomization = customization,
        currentScreen = ScreenDestination.OPEN_WORLD_GAME
      )
    }
    // Auto-save initial profile
    triggerSave()
  }

  fun handleInteractClick() {
    val target = _uiState.value.nearestTarget ?: return
    when (target) {
      is InteractableTarget.NpcTarget -> {
        _uiState.update { it.copy(activeDialogueNpc = target.npc) }
      }
      is InteractableTarget.BuildingTarget -> {
        _uiState.update { it.copy(activeBuildingLore = "${target.building.name}: ${target.building.culturalDetail}") }
      }
    }
  }

  fun dismissDialogue() {
    _uiState.update { it.copy(activeDialogueNpc = null, activeBuildingLore = null) }
  }

  fun selectDialogueChoice(choice: DialogueChoice) {
    // Reward player with wisdom, trust, reputation
    val curStats = _uiState.value.playerStats
    val newWisdom = curStats.wisdom + choice.wisdomReward
    val newTrust = (curStats.communityTrust + choice.trustReward).coerceAtMost(100)
    val newRep = curStats.reputation + (choice.trustReward * 5)

    var updatedStats = curStats.copy(
      wisdom = newWisdom,
      communityTrust = newTrust,
      reputation = newRep
    )

    // Check rank progression
    val newRank = evaluateRank(updatedStats.reputation, updatedStats.communityTrust)

    _uiState.update {
      it.copy(
        playerStats = updatedStats,
        playerRank = newRank
      )
    }

    if (choice.actionTag == "CHECK_QUEST" || choice.actionTag?.startsWith("ACCEPT") == true) {
      // Advance intro quest
      advanceQuestObjective("quest_intro_lanao", "obj_greet_datu")
    }
  }

  private fun evaluateRank(reputation: Int, trust: Int): PlayerRank {
    val ranksDescending = PlayerRank.values().sortedByDescending { it.level }
    for (r in ranksDescending) {
      if (reputation >= r.requiredReputation && trust >= r.requiredTrust) {
        return r
      }
    }
    return PlayerRank.ORDINARY_MEMBER
  }

  fun buyItem(item: GameItem) {
    val curGold = _uiState.value.playerStats.gold
    if (curGold >= item.basePrice) {
      val newGold = curGold - item.basePrice
      val newInventory = _uiState.value.inventoryItemIds + item.id
      val newKnowledge = _uiState.value.playerStats.culturalKnowledge + 5

      _uiState.update {
        it.copy(
          playerStats = it.playerStats.copy(gold = newGold, culturalKnowledge = newKnowledge),
          inventoryItemIds = newInventory
        )
      }

      // Check quest requirements (e.g. for weaving silk or boat timber)
      if (item.id == "natural_silk_spool") {
        advanceQuestObjective("quest_weaver_threads", "obj_get_silk")
      }
      if (item.id == "timber_yakal") {
        advanceQuestObjective("quest_restore_awang", "obj_get_timber")
      }
      if (item.id == "brass_ingot") {
        advanceQuestObjective("quest_restore_awang", "obj_get_brass")
      }
    }
  }

  fun sellItem(item: GameItem) {
    if (_uiState.value.inventoryItemIds.contains(item.id)) {
      val sellValue = (item.basePrice * 0.7f).toInt()
      val newGold = _uiState.value.playerStats.gold + sellValue
      val newInventory = _uiState.value.inventoryItemIds.toMutableList().apply { remove(item.id) }

      _uiState.update {
        it.copy(
          playerStats = it.playerStats.copy(gold = newGold),
          inventoryItemIds = newInventory
        )
      }
    }
  }

  fun enactCouncilPolicy(policy: CouncilPolicy) {
    val kStats = _uiState.value.kingdomStats
    if (kStats.treasury >= policy.goldCost && !policy.isEnacted) {
      val newPolicies = kStats.policies.map {
        if (it.id == policy.id) it.copy(isEnacted = true) else it
      }
      val updatedKingdom = kStats.copy(
        treasury = kStats.treasury - policy.goldCost,
        villageHappiness = (kStats.villageHappiness + policy.happinessBoost).coerceAtMost(100),
        culturalHeritageScore = (kStats.culturalHeritageScore + policy.heritageBoost).coerceAtMost(100),
        infrastructureLevel = (kStats.infrastructureLevel + policy.infrastructureBoost).coerceAtMost(100),
        policies = newPolicies
      )

      val newTrust = (_uiState.value.playerStats.communityTrust + policy.trustBoost).coerceAtMost(100)
      val newRep = _uiState.value.playerStats.reputation + 75
      val updatedPlayerStats = _uiState.value.playerStats.copy(
        communityTrust = newTrust,
        reputation = newRep
      )
      val newRank = evaluateRank(newRep, newTrust)

      _uiState.update {
        it.copy(
          kingdomStats = updatedKingdom,
          playerStats = updatedPlayerStats,
          playerRank = newRank,
          saveToastMessage = "Decree Enacted: ${policy.name} has improved village prosperity!"
        )
      }
    }
  }

  fun fastTravelTo(pos: Vector3) {
    worldEngine.playerPosition = pos
    worldEngine.camera.targetPosition = pos
  }

  private fun checkAutoObjectives(pos: Vector3) {
    // If near Torogan (z > 14)
    if (pos.z > 12f) {
      advanceQuestObjective("quest_intro_lanao", "obj_visit_torogan")
    }
    // If near Lake docks (z < -8)
    if (pos.z < -8f) {
      advanceQuestObjective("quest_intro_lanao", "obj_visit_lake")
    }
  }

  private fun advanceQuestObjective(questId: String, objectiveId: String) {
    val currentQuests = _uiState.value.quests
    val updatedQuests = currentQuests.map { q ->
      if (q.id == questId) {
        val updatedObjs = q.objectives.map { obj ->
          if (obj.id == objectiveId && obj.currentCount < obj.requiredCount) {
            obj.copy(currentCount = obj.currentCount + 1)
          } else obj
        }
        val allDone = updatedObjs.all { it.isFinished }
        q.copy(
          objectives = updatedObjs,
          status = if (allDone) QuestStatus.COMPLETED else q.status
        )
      } else q
    }

    _uiState.update { it.copy(quests = updatedQuests) }
  }

  fun triggerSave() {
    val state = _uiState.value
    val success = saveManager.saveGame(
      state.playerCustomization,
      state.playerStats,
      state.playerRank,
      state.inventoryItemIds,
      state.quests,
      state.kingdomStats
    )
    _uiState.update {
      it.copy(saveToastMessage = if (success) "Chronicle preserved at Lake Lanao!" else "Save failed.")
    }
  }

  fun triggerLoad() {
    val loaded = saveManager.loadGame()
    if (loaded != null) {
      _uiState.update {
        it.copy(
          playerCustomization = loaded.customization,
          playerStats = loaded.stats,
          playerRank = loaded.rank,
          inventoryItemIds = loaded.inventory,
          kingdomStats = loaded.kingdom,
          saveToastMessage = "Chronicle loaded successfully!"
        )
      }
    }
  }

  fun setGraphicsQuality(q: String) {
    _uiState.update { it.copy(graphicsQuality = q) }
  }

  fun toggleAudio(enabled: Boolean) {
    _uiState.update { it.copy(audioEnabled = enabled) }
  }

  fun setSensitivity(s: Float) {
    _uiState.update { it.copy(cameraSensitivity = s) }
  }
}
