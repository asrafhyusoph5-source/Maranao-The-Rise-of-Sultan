package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CulturalArticle(
  val title: String,
  val maranaoTerm: String,
  val category: String,
  val content: String,
  val historicalSignificance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalLibraryScreen(onBack: () -> Unit) {
  val articles = listOf(
    CulturalArticle(
      title = "The Torogan Ancestral Palace",
      maranaoTerm = "Torogan",
      category = "Architecture",
      content = "The Torogan is the traditional stately home of the highest titleholder in a Maranao community. Elevated on colossal round tree-trunk pillars resting on stone bases for earthquake resilience, it features a grand communal hall where assemblies, weddings, and judicial hearings take place.",
      historicalSignificance = "The Torogan is recognized as a National Cultural Treasure of the Philippines, symbolizing prestige, communal unity, and enduring ancestral architecture."
    ),
    CulturalArticle(
      title = "Panolong Flaring Wing Beams",
      maranaoTerm = "Panolong",
      category = "Woodcarving & Architecture",
      content = "The Panolong are intricately carved wooden beam extensions protruding from the facade of the Torogan like wings. Carved from solid hardwood, they feature the Niyaga (serpent/dragon) and Dapa (fern/leaf) okir motifs.",
      historicalSignificance = "The Panolong is the most iconic visual signature of Maranao artistic brilliance, representing protection, soaring nobility, and harmony with nature."
    ),
    CulturalArticle(
      title = "Okir Artistic Tradition",
      maranaoTerm = "Okir / Okil",
      category = "Art & Geometry",
      content = "Okir refers to the geometric and flowing curvilinear art traditions of the Maranao. Okir a Datu features organic spiral, leaf, and floral motifs (such as matilak and dapa), while Okir a Bay refers to geometric textile zigzag and diamond patterns.",
      historicalSignificance = "Okir ornamentation adorns Torogans, brass kulintang stands, boats, weapons, and household chests, demonstrating geometric sophistication passed through apprentice guilds."
    ),
    CulturalArticle(
      title = "Malong Landap & Langkit",
      maranaoTerm = "Landap ago Langkit",
      category = "Textiles & Weaving",
      content = "The Malong Landap is the most formal Maranao malong, constructed of two primary panels joined by the Langkit — an intricate, tapestry-woven strip created on backstrap looms. Colors carry deep meaning: yellow and gold denote royalty (Pegawidan), green represents peace and piety, while red honors valor.",
      historicalSignificance = "Woven exclusively for ceremonial gatherings, the Landap is an irreplaceable treasure of Philippine textile heritage."
    ),
    CulturalArticle(
      title = "Kulintang Gong Music Tradition",
      maranaoTerm = "Kulintang Ensemble",
      category = "Music & Heritage",
      content = "The Kulintang is an ancient gong-chime ensemble composed of a row of eight graduated bossed brass gongs laid horizontally on an ornate wooden stand, accompanied by the agong, gandingan, babandir, and the carved dabakan goblet drum.",
      historicalSignificance = "Kulintang music has resonated across Mindanao for over a millennium, expressing social celebration, courtship, and spiritual devotion without relying on written notation."
    ),
    CulturalArticle(
      title = "The Four Principalities of Lanao",
      maranaoTerm = "Pat a Pangampong ko Ranao",
      category = "Traditional Governance",
      content = "Traditional Maranao society is organized into the Four Principalities (Pangampong): Bayabao, Masiu, Unayan, and Baloi. Each principality comprises multiple royal houses (Pegawidan) and supporting lineages governed by customary law (Taritib) and consensus (Ijma).",
      historicalSignificance = "The confederation ensured regional defense, economic trade, and peaceful dispute arbitration around Lake Lanao centuries before modern colonial times."
    ),
    CulturalArticle(
      title = "The Sacred Title of Sultan",
      maranaoTerm = "Kadatuan ago Kapaginged",
      category = "Leadership & Responsibility",
      content = "In authentic Maranao political philosophy, a Sultan is not an absolute monarch. He is a servant-leader bound by kinship obligations, Islamic ethics, and customary law. The Sultan mediates community disputes, guarantees hospitality to travelers, and protects the vulnerable.",
      historicalSignificance = "True leadership in Lanao is measured not by wealth, but by the prosperity, justice, and spiritual well-being of the community."
    )
  )

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Lake Lanao Cultural Archive", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Verified Maranao Heritage & Traditions", color = TextSecondary, fontSize = 12.sp)
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_library_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextGold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = SurfaceContainerDark,
          titleContentColor = TextGold
        )
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(articles) { article ->
        Card(
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = article.category, color = MaranaoGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text(text = article.maranaoTerm, color = TextGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Text(text = article.title, color = TextGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Text(text = article.content, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)

            Surface(
              color = SurfaceContainerDark,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Significance: ${article.historicalSignificance}",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }
    }
  }
}
