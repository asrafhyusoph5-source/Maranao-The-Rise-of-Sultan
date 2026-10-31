package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class GameSaveManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("maranao_game_save", Context.MODE_PRIVATE)

  fun saveGame(
    customization: PlayerCustomization,
    stats: PlayerStats,
    rank: PlayerRank,
    inventory: List<String>,
    quests: List<Quest>,
    kingdom: KingdomStats
  ): Boolean {
    return try {
      val json = JSONObject().apply {
        // Player customization
        put("name", customization.name)
        put("gender", customization.gender.name)
        put("skinToneIndex", customization.skinToneIndex)
        put("headwear", customization.headwear)
        put("upperClothing", customization.upperClothing)
        put("lowerClothing", customization.lowerClothing)
        put("accessory", customization.accessory)

        // Stats
        put("gold", stats.gold)
        put("reputation", stats.reputation)
        put("communityTrust", stats.communityTrust)
        put("wisdom", stats.wisdom)
        put("culturalKnowledge", stats.culturalKnowledge)

        // Rank
        put("rankLevel", rank.level)

        // Inventory
        val invArray = JSONArray()
        inventory.forEach { invArray.put(it) }
        put("inventory", invArray)

        // Kingdom stats
        put("treasury", kingdom.treasury)
        put("population", kingdom.population)
        put("villageHappiness", kingdom.villageHappiness)
        put("foodReserves", kingdom.foodReserves)
        put("culturalHeritageScore", kingdom.culturalHeritageScore)
        put("infrastructureLevel", kingdom.infrastructureLevel)

        val enactedArray = JSONArray()
        kingdom.policies.filter { it.isEnacted }.forEach { enactedArray.put(it.id) }
        put("enactedPolicies", enactedArray)

        put("saveTimestamp", System.currentTimeMillis())
      }

      prefs.edit().putString("save_slot_1", json.toString()).apply()
      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  fun hasSavedGame(): Boolean = prefs.contains("save_slot_1")

  fun loadGame(): LoadedGameData? {
    return try {
      val raw = prefs.getString("save_slot_1", null) ?: return null
      val json = JSONObject(raw)

      val custom = PlayerCustomization(
        gender = Gender.valueOf(json.optString("gender", Gender.MALE.name)),
        name = json.optString("name", "Al-Rashid"),
        skinToneIndex = json.optInt("skinToneIndex", 1),
        headwear = json.optString("headwear", "Tubao Headband"),
        upperClothing = json.optString("upperClothing", "Embroidered Bating Vest"),
        lowerClothing = json.optString("lowerClothing", "Malong Landap (Yellow & Red)"),
        accessory = json.optString("accessory", "Brass Betel Box")
      )

      val stats = PlayerStats(
        gold = json.optInt("gold", 120),
        reputation = json.optInt("reputation", 50),
        communityTrust = json.optInt("communityTrust", 15),
        wisdom = json.optInt("wisdom", 20),
        culturalKnowledge = json.optInt("culturalKnowledge", 30)
      )

      val rankLevel = json.optInt("rankLevel", 1)
      val rank = PlayerRank.values().find { it.level == rankLevel } ?: PlayerRank.ORDINARY_MEMBER

      val invArray = json.optJSONArray("inventory")
      val inventory = mutableListOf<String>()
      if (invArray != null) {
        for (i in 0 until invArray.length()) {
          inventory.add(invArray.getString(i))
        }
      }

      val enactedArray = json.optJSONArray("enactedPolicies")
      val enactedIds = mutableSetOf<String>()
      if (enactedArray != null) {
        for (i in 0 until enactedArray.length()) {
          enactedIds.add(enactedArray.getString(i))
        }
      }

      val baseKingdom = KingdomStats()
      val kingdom = KingdomStats(
        treasury = json.optInt("treasury", 850),
        population = json.optInt("population", 1420),
        villageHappiness = json.optInt("villageHappiness", 78),
        foodReserves = json.optInt("foodReserves", 180),
        culturalHeritageScore = json.optInt("culturalHeritageScore", 65),
        infrastructureLevel = json.optInt("infrastructureLevel", 55),
        policies = baseKingdom.policies.map { p ->
          if (enactedIds.contains(p.id)) p.copy(isEnacted = true) else p
        }
      )

      LoadedGameData(custom, stats, rank, inventory, kingdom)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}

data class LoadedGameData(
  val customization: PlayerCustomization,
  val stats: PlayerStats,
  val rank: PlayerRank,
  val inventory: List<String>,
  val kingdom: KingdomStats
)
