package com.example.model

enum class ItemCategory {
  CLOTHING,
  INSTRUMENT,
  HEIRLOOM,
  TRADE_GOOD,
  CRAFTING_MATERIAL,
  FOOD
}

data class GameItem(
  val id: String,
  val name: String,
  val maranaoName: String,
  val category: ItemCategory,
  val description: String,
  val culturalLore: String,
  val basePrice: Int,
  val iconEmoji: String,
  val isEquippable: Boolean = false,
  val isCeremonial: Boolean = false
)

object CulturalItemDatabase {
  val allItems: List<GameItem> = listOf(
    GameItem(
      id = "malong_landap_gold",
      name = "Malong Landap (Yellow & Red)",
      maranaoName = "Landap a Gadong/Mariga",
      category = ItemCategory.CLOTHING,
      description = "A royal tubular woven garment featuring intricate vertical langkit tapestry bands.",
      culturalLore = "The Malong Landap is the most prestigious Maranao malong, traditionally reserved for nobility (Pegawidan) during weddings and grand gatherings.",
      basePrice = 180,
      iconEmoji = "👘",
      isEquippable = true,
      isCeremonial = true
    ),
    GameItem(
      id = "malong_andon",
      name = "Malong Andon (Geometric)",
      maranaoName = "Malong Andon",
      category = ItemCategory.CLOTHING,
      description = "A finely woven malong featuring resist-dyed geometric motifs inspired by ancient tie-dye weaving.",
      culturalLore = "One of the rarest traditional woven treasures of Lake Lanao, requiring months of meticulous natural dye work.",
      basePrice = 140,
      iconEmoji = "🧵",
      isEquippable = true
    ),
    GameItem(
      id = "kulintang_gong",
      name = "Brass Kulintang Gong",
      maranaoName = "Kulintang",
      category = ItemCategory.INSTRUMENT,
      description = "A bossed bronze/brass kettle gong with a resonant, melodious tone.",
      culturalLore = "Part of the ancient Kulintang ensemble, an indigenous gong-chime music tradition passed down through generations in Mindanao.",
      basePrice = 250,
      iconEmoji = "🔔",
      isCeremonial = true
    ),
    GameItem(
      id = "dabakan_drum",
      name = "Carved Dabakan Drum",
      maranaoName = "Dabakan",
      category = ItemCategory.INSTRUMENT,
      description = "An hourglass-shaped wooden drum adorned with flowing Okir leaf carvings.",
      culturalLore = "The only non-gong instrument in the Maranao kulintang ensemble, providing dynamic rhythmic cadence.",
      basePrice = 200,
      iconEmoji = "🪘",
      isCeremonial = true
    ),
    GameItem(
      id = "salapa_box",
      name = "Brass Betel Box",
      maranaoName = "Salapa",
      category = ItemCategory.HEIRLOOM,
      description = "A heavy brass container inlaid with silver geometric Okir motifs.",
      culturalLore = "Traditionally used by Maranao elders and dignitaries for hospitality when welcoming visitors to the Torogan.",
      basePrice = 110,
      iconEmoji = "📦",
      isEquippable = true
    ),
    GameItem(
      id = "kris_ceremonial",
      name = "Ceremonial Kris Sash",
      maranaoName = "Kalis",
      category = ItemCategory.HEIRLOOM,
      description = "A waved ceremonial heirloom blade scabbard wrapped in gold thread.",
      culturalLore = "The Kris symbolizes ancestral defense, honor, and leadership responsibility in the Sultanate.",
      basePrice = 300,
      iconEmoji = "🗡️",
      isCeremonial = true
    ),
    GameItem(
      id = "lake_lanao_pearl",
      name = "Lanao Freshwater Pearl",
      maranaoName = "Mutiya",
      category = ItemCategory.TRADE_GOOD,
      description = "A gleaming natural freshwater pearl found in Lake Lanao's peaceful bays.",
      culturalLore = "Highly prized in traditional jewelry and as gifts during diplomatic exchanges among Datus.",
      basePrice = 90,
      iconEmoji = "🦪"
    ),
    GameItem(
      id = "dodol_sweet",
      name = "Traditional Dodol",
      maranaoName = "Dodol",
      category = ItemCategory.FOOD,
      description = "A rich, chewy Maranao sweet delicacy made from coconut milk, sugarcane, and sticky rice.",
      culturalLore = "Prepared in large brass vats over slow fire, Dodol is the hallmark dessert of Maranao festivals and celebrations.",
      basePrice = 15,
      iconEmoji = "🍬"
    ),
    GameItem(
      id = "dried_lake_fish",
      name = "Lake Lanao Dried Fish",
      maranaoName = "Kasili / Bakar",
      category = ItemCategory.FOOD,
      description = "Sun-cured freshwater fish seasoned with lake sea-salt and native herbs.",
      culturalLore = "A staple food of lakeside communities, sustaining fishermen and boatbuilders across Lake Lanao.",
      basePrice = 20,
      iconEmoji = "🐟"
    ),
    GameItem(
      id = "natural_silk_spool",
      name = "Hand-Dyed Silk Spool",
      maranaoName = "Sutla",
      category = ItemCategory.CRAFTING_MATERIAL,
      description = "Spun silk dyed using turmeric, bark, and indigo lake plants.",
      culturalLore = "Used on traditional backstrap looms to weave the vibrant langkit bands.",
      basePrice = 35,
      iconEmoji = "🧶"
    ),
    GameItem(
      id = "timber_yakal",
      name = "Seasoned Yakal Timber",
      maranaoName = "Papan a Yakal",
      category = ItemCategory.CRAFTING_MATERIAL,
      description = "Dense hardwood timber ideal for Torogan stilt posts and boat frames.",
      culturalLore = "Maranao architecture relies on durable tropical hardwoods capable of weathering decades without nails.",
      basePrice = 45,
      iconEmoji = "🪵"
    ),
    GameItem(
      id = "brass_ingot",
      name = "Melted Brass Ingot",
      maranaoName = "Kasa a Tombaga",
      category = ItemCategory.CRAFTING_MATERIAL,
      description = "Purified brass alloy prepared for casting and chasing by master artisans (Panday).",
      culturalLore = "The Tugaya craftsmen of Lake Lanao are world-renowned for their lost-wax brass-casting heritage.",
      basePrice = 60,
      iconEmoji = "🪙"
    )
  )

  fun getItem(id: String): GameItem? = allItems.find { it.id == id }
}
