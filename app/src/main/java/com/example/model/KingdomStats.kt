package com.example.model

data class CouncilPolicy(
  val id: String,
  val name: String,
  val maranaoName: String,
  val description: String,
  val goldCost: Int,
  val foodCost: Int = 0,
  val happinessBoost: Int,
  val trustBoost: Int,
  val heritageBoost: Int,
  val infrastructureBoost: Int,
  val isEnacted: Boolean = false
)

data class KingdomStats(
  val treasury: Int = 850,
  val population: Int = 1420,
  val villageHappiness: Int = 78, // percentage 0-100
  val foodReserves: Int = 180,
  val culturalHeritageScore: Int = 65, // percentage 0-100
  val infrastructureLevel: Int = 55, // percentage 0-100
  val policies: List<CouncilPolicy> = listOf(
    CouncilPolicy(
      id = "policy_weavers",
      name = "Guild Grant for Malong Weavers & Artisans",
      maranaoName = "Tabang ko mga Panday",
      description = "Provide silk yarns and brass alloys to village guildmasters, preserving ancient okir patterns.",
      goldCost = 120,
      happinessBoost = 10,
      trustBoost = 8,
      heritageBoost = 15,
      infrastructureBoost = 5
    ),
    CouncilPolicy(
      id = "policy_fishing_fleet",
      name = "Expand Lake Awang Fishing Fleet",
      maranaoName = "Pakaolad ko mga Awang",
      description = "Commission 10 new outrigger canoes from Panday Bilal to harvest deeper lake fisheries.",
      goldCost = 90,
      happinessBoost = 8,
      trustBoost = 6,
      heritageBoost = 5,
      infrastructureBoost = 15
    ),
    CouncilPolicy(
      id = "policy_torogan_preserve",
      name = "Restore Ancestral Torogan Beams & Panolong",
      maranaoName = "Kapipiya ko Torogan",
      description = "Reinforce sacred timber pillars and repaint the soaring okir carvings against lake humidity.",
      goldCost = 160,
      happinessBoost = 12,
      trustBoost = 15,
      heritageBoost = 20,
      infrastructureBoost = 20
    ),
    CouncilPolicy(
      id = "policy_heritage_school",
      name = "Establish Lake Heritage Learning Center",
      maranaoName = "Madrasah ko Ranao",
      description = "Found a cultural academy where youth learn kulintang music, traditional governance, and calligraphy.",
      goldCost = 140,
      happinessBoost = 14,
      trustBoost = 12,
      heritageBoost = 25,
      infrastructureBoost = 10
    ),
    CouncilPolicy(
      id = "policy_kanduri_feast",
      name = "Host Grand Unity Gathering (Kanduri)",
      maranaoName = "Kanduri ko Kapaginged",
      description = "Distribute yellow turmeric rice (Kuning), roasted fish, and dodol sweetmeats to all families.",
      goldCost = 110,
      foodCost = 40,
      happinessBoost = 25,
      trustBoost = 18,
      heritageBoost = 10,
      infrastructureBoost = 0
    )
  )
)
