package com.example.model

enum class PlayerRank(
  val level: Int,
  val maranaoTitle: String,
  val englishTitle: String,
  val requiredReputation: Int,
  val requiredTrust: Int,
  val description: String
) {
  ORDINARY_MEMBER(
    level = 1,
    maranaoTitle = "Katatagoan",
    englishTitle = "Ordinary Community Member",
    requiredReputation = 0,
    requiredTrust = 0,
    description = "A humble young resident of Lake Lanao. Begin by learning traditions and helping neighbors."
  ),
  RESPECTED_MEMBER(
    level = 2,
    maranaoTitle = "Kasasalonga",
    englishTitle = "Respected Community Member",
    requiredReputation = 100,
    requiredTrust = 25,
    description = "Recognized by elders for honesty, cultural respect, and service to the village."
  ),
  COMMUNITY_LEADER(
    level = 3,
    maranaoTitle = "Kapangongona",
    englishTitle = "Community Leader",
    requiredReputation = 250,
    requiredTrust = 45,
    description = "Entrusted with organizing community projects, mediating minor matters, and trade initiatives."
  ),
  DATU(
    level = 4,
    maranaoTitle = "Datu",
    englishTitle = "District Leader (Datu)",
    requiredReputation = 500,
    requiredTrust = 65,
    description = "A respected noble leader holding council in a local Torogan. Guides families and local commerce."
  ),
  ROYAL_COUNCIL_MEMBER(
    level = 5,
    maranaoTitle = "Pangampong Advisor",
    englishTitle = "Royal Council Elder",
    requiredReputation = 800,
    requiredTrust = 80,
    description = "Influential representative in the council of the Pat a Pangampong ko Ranao. Advises on regional peace."
  ),
  SULTAN(
    level = 6,
    maranaoTitle = "Sultan ko Ranao",
    englishTitle = "Sultan of the Realm",
    requiredReputation = 1200,
    requiredTrust = 95,
    description = "The supreme leadership title earned through dedication, community trust, wisdom, and moral responsibility."
  )
}

enum class Gender {
  MALE, FEMALE
}

data class PlayerCustomization(
  val gender: Gender = Gender.MALE,
  val name: String = "Al-Rashid",
  val skinToneIndex: Int = 1, // 0 to 3 (warm tropical shades)
  val headwear: String = "Tubao Headband",
  val upperClothing: String = "Embroidered Bating Vest",
  val lowerClothing: String = "Malong Landap (Yellow & Red)",
  val accessory: String = "Brass Betel Box"
)

data class PlayerStats(
  val gold: Int = 120, // Traditional Maranao coins / currency
  val reputation: Int = 50,
  val communityTrust: Int = 15,
  val wisdom: Int = 20,
  val culturalKnowledge: Int = 30,
  val energy: Float = 100f
)
