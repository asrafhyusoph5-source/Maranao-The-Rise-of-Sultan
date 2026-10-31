package com.example.model

enum class QuestStatus {
  NOT_STARTED,
  IN_PROGRESS,
  READY_TO_TURN_IN,
  COMPLETED
}

data class QuestObjective(
  val id: String,
  val description: String,
  val currentCount: Int,
  val requiredCount: Int,
  val isFinished: Boolean = currentCount >= requiredCount
)

data class Quest(
  val id: String,
  val chapter: Int,
  val title: String,
  val maranaoTitle: String,
  val giverName: String,
  val giverId: String,
  val summary: String,
  val culturalSignificance: String,
  val objectives: List<QuestObjective>,
  val status: QuestStatus = QuestStatus.NOT_STARTED,
  val goldReward: Int,
  val reputationReward: Int,
  val trustReward: Int,
  val wisdomReward: Int,
  val itemRewardId: String? = null
)

object QuestDatabase {
  val initialQuests: List<Quest> = listOf(
    Quest(
      id = "quest_intro_lanao",
      chapter = 1,
      title = "Awakening at Lake Lanao",
      maranaoTitle = "Kapanompang ko Ranao",
      giverName = "Datu Ali",
      giverId = "datu_ali",
      summary = "Pay your respects to the village elder, visit the ancestral Torogan house, and learn about the sacred duties of community members.",
      culturalSignificance = "In Maranao community life, one begins by seeking the blessing and wisdom of the elders (Kapamangasi).",
      objectives = listOf(
        QuestObjective("obj_greet_datu", "Speak with Elder Datu Ali at the Torogan courtyard", 1, 1),
        QuestObjective("obj_visit_torogan", "Inspect the ancestral Torogan with carved Panolong beams", 0, 1),
        QuestObjective("obj_visit_lake", "Walk down to the tranquil shores of Lake Lanao", 0, 1)
      ),
      status = QuestStatus.IN_PROGRESS,
      goldReward = 50,
      reputationReward = 40,
      trustReward = 15,
      wisdomReward = 20,
      itemRewardId = "salapa_box"
    ),

    Quest(
      id = "quest_weaver_threads",
      chapter = 2,
      title = "The Sacred Threads of Langkit",
      maranaoTitle = "So Bontal ko Langkit",
      giverName = "Bae Fatima",
      giverId = "bae_fatima",
      summary = "Assist Master Weaver Bae Fatima by collecting natural lake reeds and silk dyes to complete the ceremonial Malong Landap.",
      culturalSignificance = "Weaving is regarded as a sacred heritage in Lanao. Patterns like Okir are mathematical and spiritual reflections of cosmos and nature.",
      objectives = listOf(
        QuestObjective("obj_get_silk", "Obtain Hand-Dyed Silk Spools for the loom", 0, 2),
        QuestObjective("obj_learn_okir", "Learn the three sacred colors of the Malong Landap", 0, 1)
      ),
      status = QuestStatus.NOT_STARTED,
      goldReward = 80,
      reputationReward = 75,
      trustReward = 25,
      wisdomReward = 30,
      itemRewardId = "malong_landap_gold"
    ),

    Quest(
      id = "quest_restore_awang",
      chapter = 2,
      title = "Restoring the Village Awang",
      maranaoTitle = "Kapangonor ko Awang",
      giverName = "Panday Bilal",
      giverId = "panday_bilal",
      summary = "Help Panday Bilal gather seasoned timber and brass rivets to repair the village fishing and ceremonial boat.",
      culturalSignificance = "The Awang is essential for transport across the vast waters of Lake Lanao, connecting isolated lakeside communities.",
      objectives = listOf(
        QuestObjective("obj_get_timber", "Secure Seasoned Yakal Timber from the artisan district", 0, 2),
        QuestObjective("obj_get_brass", "Obtain Melted Brass Ingots for durable hull brackets", 0, 1)
      ),
      status = QuestStatus.NOT_STARTED,
      goldReward = 120,
      reputationReward = 100,
      trustReward = 30,
      wisdomReward = 25,
      itemRewardId = "kulintang_gong"
    ),

    Quest(
      id = "quest_council_datu",
      chapter = 3,
      title = "The Assembly of the Datus",
      maranaoTitle = "So Kapipiya ko Pagtao",
      giverName = "Datu Ali",
      giverId = "datu_ali",
      summary = "A trade dispute between the lakeside fishermen and the upland weavers requires a fair and wise mediator. Resolve it in the village council.",
      culturalSignificance = "Traditional Maranao conflict resolution (Taritib) prioritizes reconciliation, family harmony, and community peace over punishment.",
      objectives = listOf(
        QuestObjective("obj_meet_fisher", "Listen to the fishermen at the docks", 0, 1),
        QuestObjective("obj_meet_weaver", "Listen to the weavers at the pavilion", 0, 1),
        QuestObjective("obj_council_ruling", "Propose a fair community trade exchange decree", 0, 1)
      ),
      status = QuestStatus.NOT_STARTED,
      goldReward = 200,
      reputationReward = 200,
      trustReward = 35,
      wisdomReward = 45,
      itemRewardId = "kris_ceremonial"
    ),

    Quest(
      id = "quest_rise_of_sultan",
      chapter = 6,
      title = "The Coronation of the Sultan",
      maranaoTitle = "Kapagalad ko Sultan",
      giverName = "Royal Council of Lanao",
      giverId = "datu_ali",
      summary = "Having proven your wisdom, honor, selflessness, and economic stewardship across all four principalities, face the Royal Council for unanimous selection as Sultan.",
      culturalSignificance = "Becoming Sultan in Maranao tradition is an oath of servitude to Allah and the people. The Sultan is a protector (Pegawidan), not a ruler of vanity.",
      objectives = listOf(
        QuestObjective("obj_reach_datu", "Attain the rank of Datu / Council Leader", 0, 1),
        QuestObjective("obj_high_trust", "Achieve 90% Community Trust rating", 0, 1),
        QuestObjective("obj_take_oath", "Take the Sacred Oath of Leadership in the Royal Council Torogan", 0, 1)
      ),
      status = QuestStatus.NOT_STARTED,
      goldReward = 500,
      reputationReward = 500,
      trustReward = 50,
      wisdomReward = 60,
      itemRewardId = "kris_ceremonial"
    )
  )
}
