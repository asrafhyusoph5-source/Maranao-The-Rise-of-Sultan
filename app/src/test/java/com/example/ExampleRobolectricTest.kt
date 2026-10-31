package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameSaveManager
import com.example.engine.WorldEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Maranao: Rise of Sultan", appName)
  }

  @Test
  fun `verify world engine buildings and collision boundaries`() {
    val engine = WorldEngine()
    // Ancestral Torogan exists in building list
    val torogan = engine.buildings.find { it.type == com.example.engine.WorldObjectType.TOROGAN_PALACE }
    assertNotNull(torogan)
    assertEquals("Kialdan a Torogan (Ancestral Palace)", torogan?.name)

    // Moving directly into building center is prevented
    assertFalse(engine.canMoveTo(torogan!!.position.x, torogan.position.z))

    // Open ground movement is allowed
    assertTrue(engine.canMoveTo(0f, 0f))
  }

  @Test
  fun `verify cultural item database and prices`() {
    val malong = CulturalItemDatabase.getItem("malong_landap_gold")
    assertNotNull(malong)
    assertTrue(malong!!.isEquippable)
    assertTrue(malong.isCeremonial)
    assertEquals("Landap a Gadong/Mariga", malong.maranaoName)

    val kulintang = CulturalItemDatabase.getItem("kulintang_gong")
    assertNotNull(kulintang)
    assertEquals(ItemCategory.INSTRUMENT, kulintang?.category)
  }

  @Test
  fun `verify npc database and cultural wisdom`() {
    val datuAli = NPCDatabase.getNPC("datu_ali")
    assertNotNull(datuAli)
    assertEquals("Datu Ali", datuAli?.name)
    assertTrue(datuAli!!.dialogNode.culturalWisdom.contains("Taritib ago Ijma"))

    val baeFatima = NPCDatabase.getNPC("bae_fatima")
    assertNotNull(baeFatima)
    assertTrue(baeFatima!!.isMerchant)

    val pandayBilal = NPCDatabase.getNPC("panday_bilal")
    assertNotNull(pandayBilal)
  }

  @Test
  fun `verify save manager persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val saveManager = GameSaveManager(context)

    val custom = PlayerCustomization(name = "Datu Mamasao")
    val stats = PlayerStats(gold = 250, reputation = 150, communityTrust = 35)
    val rank = PlayerRank.RESPECTED_MEMBER
    val inventory = listOf("salapa_box", "kulintang_gong")
    val kingdom = KingdomStats(treasury = 950)

    val saved = saveManager.saveGame(custom, stats, rank, inventory, emptyList(), kingdom)
    assertTrue(saved)
    assertTrue(saveManager.hasSavedGame())

    val loaded = saveManager.loadGame()
    assertNotNull(loaded)
    assertEquals("Datu Mamasao", loaded?.customization?.name)
    assertEquals(250, loaded?.stats?.gold)
    assertEquals(PlayerRank.RESPECTED_MEMBER, loaded?.rank)
    assertEquals(2, loaded?.inventory?.size)
  }
}
