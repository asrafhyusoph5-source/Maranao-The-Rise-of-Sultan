package com.example.model

data class DialogueChoice(
  val label: String,
  val responseText: String,
  val trustReward: Int = 0,
  val wisdomReward: Int = 0,
  val actionTag: String? = null // e.g., "ACCEPT_QUEST", "OPEN_SHOP", "LEARN_LORE"
)

data class DialogueNode(
  val greeting: String,
  val culturalWisdom: String,
  val choices: List<DialogueChoice>
)

data class NPC(
  val id: String,
  val name: String,
  val title: String,
  val maranaoTitle: String,
  val role: String,
  val locationName: String,
  val worldX: Float,
  val worldZ: Float,
  val primaryColorHex: Long,
  val dialogNode: DialogueNode,
  val questIdOffered: String? = null,
  val isMerchant: Boolean = false
)

object NPCDatabase {
  val villagers: List<NPC> = listOf(
    NPC(
      id = "datu_ali",
      name = "Datu Ali",
      title = "Village Elder & Council Guardian",
      maranaoTitle = "Datu ko Ingud",
      role = "Guardian of Torogan & Traditional Governance",
      locationName = "Torogan Palace Courtyard",
      worldX = 0f,
      worldZ = 12f,
      primaryColorHex = 0xFF801A24, // Royal crimson
      dialogNode = DialogueNode(
        greeting = "Assalamu Alaikum, young traveler. Welcome to our ancestral Torogan on the shores of Lake Lanao.",
        culturalWisdom = "Remember: in Maranao tradition, true Kadatuan (chieftainship) is never about commanding others. It is about Taritib ago Ijma — customary order, consensus, and safeguarding the welfare of all families under Lake Lanao.",
        choices = listOf(
          DialogueChoice(
            label = "How do I earn the respect of the community?",
            responseText = "Respect is earned by deeds, not words. Aid Bae Fatima at the loom, help Panday Bilal repair our Awang boats, and listen to the concerns of the people at the marketplace.",
            wisdomReward = 10,
            trustReward = 5
          ),
          DialogueChoice(
            label = "Tell me about this grand Torogan house.",
            responseText = "The Torogan is the communal sanctuary of our clan. Look upon the carved Panolong — the flaring wing-like beams carved with the Niyaga (serpent) and Dapa (fern) motifs. They ward against ill fortune and represent the soaring spirit of our people.",
            wisdomReward = 15,
            trustReward = 5
          ),
          DialogueChoice(
            label = "I am ready to undertake the trials of leadership.",
            responseText = "A bold heart! First you must prove your loyalty to the village community. Begin by learning the crafts of the lake.",
            actionTag = "CHECK_QUEST"
          )
        )
      ),
      questIdOffered = "quest_intro_lanao"
    ),

    NPC(
      id = "bae_fatima",
      name = "Bae Fatima",
      title = "Master Weaver of Malong Landap",
      maranaoTitle = "Guro ko Langkit",
      role = "Keeper of Sacred Weaving & Okir Geometry",
      locationName = "Lakeside Weaving Pavilion",
      worldX = -14f,
      worldZ = 4f,
      primaryColorHex = 0xFF1F593E, // Emerald green
      dialogNode = DialogueNode(
        greeting = "Mapiya r'gaw! (Good day!) Welcome to our weaving circle. Listen to the gentle click of the backstrap loom.",
        culturalWisdom = "Each strand of the Malong Landap tells an ancient story. The central woven band is called the Langkit — either the wider Lakowangan or the narrower Tobiran. Its Okir patterns celebrate the harmony of nature and sky.",
        choices = listOf(
          DialogueChoice(
            label = "What do the colors of the Malong symbolize?",
            responseText = "Yellow and gold embody royal nobility (Pegawidan). Green reflects peace, spiritual devotion, and the fertile hills of Mindanao. Red stands for valor and the fiery heart of our ancestors.",
            wisdomReward = 15,
            trustReward = 5
          ),
          DialogueChoice(
            label = "May I assist you with your weaving materials?",
            responseText = "Alhamdulillah! Our loom requires hand-dyed silk thread and natural reeds harvested along Lake Lanao's shores.",
            actionTag = "ACCEPT_WEAVING_QUEST"
          ),
          DialogueChoice(
            label = "May I view your traditional textiles?",
            responseText = "Gladly! Authentic garments woven with devotion honor both the maker and the wearer.",
            actionTag = "OPEN_WEAVER_SHOP"
          )
        )
      ),
      questIdOffered = "quest_weaver_threads",
      isMerchant = true
    ),

    NPC(
      id = "panday_bilal",
      name = "Panday Bilal",
      title = "Master Brass-Smith & Boatbuilder",
      maranaoTitle = "Panday a Tombaga",
      role = "Artisan of Kulintang Gongs & Awang Boats",
      locationName = "Master Artisan Forge & Boatyard",
      worldX = 14f,
      worldZ = -4f,
      primaryColorHex = 0xFFE5A93C, // Maranao Gold
      dialogNode = DialogueNode(
        greeting = "Welcome, traveler! Smell the embers of charcoal and hear the ringing of tuned brass gongs.",
        culturalWisdom = "A true Panday shapes metal not with brute force, but with an attuned ear. When casting a Kulintang gong, the boss must ring true with harmonic overtones that unite our people in celebration.",
        choices = listOf(
          DialogueChoice(
            label = "What is the secret of the Awang boat?",
            responseText = "Our Awang dugout canoes are carved from single trunks of mountain hardwood, fitted with bamboo outriggers. They skim gracefully across Lake Lanao's waters even during sudden mountain squalls.",
            wisdomReward = 10,
            trustReward = 5
          ),
          DialogueChoice(
            label = "Do you have any tasks for an aspiring apprentice?",
            responseText = "The village ceremonial Awang needs new brass fittings and seasoned timber. Help me gather them from the artisan district!",
            actionTag = "ACCEPT_BOAT_QUEST"
          ),
          DialogueChoice(
            label = "Inspect the forged brassware and instruments.",
            responseText = "Touch the brass with respect; every piece carries the maker's soul and legacy.",
            actionTag = "OPEN_FORGE_SHOP"
          )
        )
      ),
      questIdOffered = "quest_restore_awang",
      isMerchant = true
    ),

    NPC(
      id = "gani_fisherman",
      name = "Gani",
      title = "Lake Lanao Fisherman & Trader",
      maranaoTitle = "Pa-ngingisda ko Ranao",
      role = "Navigator of the Deep Lake Waters",
      locationName = "Lake Lanao Shore Docks",
      worldX = 2f,
      worldZ = -16f,
      primaryColorHex = 0xFF1E3F5A, // Lake blue
      dialogNode = DialogueNode(
        greeting = "Peace be upon you! The lake waters are calm today, reflecting the emerald peaks of the mountains.",
        culturalWisdom = "Lake Lanao (Ranao) is the heart of the Maranao people — our very name means 'People of the Lake'. She feeds our families and connects the Four Principalities.",
        choices = listOf(
          DialogueChoice(
            label = "How has the catch been today?",
            responseText = "Abundant! The freshwater fish (Kasili) are swimming near the reed beds. Fresh fish sustains our village and trades well at the market.",
            wisdomReward = 10,
            trustReward = 5
          ),
          DialogueChoice(
            label = "Trade with Gani for lake goods and pearls.",
            responseText = "I have fresh dried fish, sweet Dodol from my family, and shimmering freshwater pearls.",
            actionTag = "OPEN_FISHERMAN_SHOP"
          )
        )
      ),
      isMerchant = true
    )
  )

  fun getNPC(id: String): NPC? = villagers.find { it.id == id }
}
