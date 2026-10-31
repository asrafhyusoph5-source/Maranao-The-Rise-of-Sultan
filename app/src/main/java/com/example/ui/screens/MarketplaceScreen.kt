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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CulturalItemDatabase
import com.example.model.GameItem
import com.example.model.ItemCategory
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
  playerGold: Int,
  inventoryItemIds: List<String>,
  onBuyItem: (GameItem) -> Unit,
  onSellItem: (GameItem) -> Unit,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Buy, 1: Sell / Inventory
  var filterCategory by remember { mutableStateOf<ItemCategory?>(null) }

  Scaffold(
    containerColor = SurfaceDark,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Lanao Marketplace & Bazaar", color = TextGold, fontWeight = FontWeight.Bold)
            Text("Treasures, Textiles, and Brassware", color = TextSecondary, fontSize = 12.sp)
          }
        },
        actions = {
          Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.5f)),
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Text(
              text = "💰 $playerGold Dirhams",
              color = MaranaoGoldLight,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_market_back")) {
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = SurfaceCard,
        contentColor = MaranaoGold,
        modifier = Modifier.border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
      ) {
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Buy Goods") })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("My Bag (${inventoryItemIds.size})") })
      }

      Spacer(modifier = Modifier.height(14.dp))

      val itemsToDisplay = if (selectedTab == 0) {
        CulturalItemDatabase.allItems
      } else {
        inventoryItemIds.mapNotNull { id -> CulturalItemDatabase.getItem(id) }
      }

      if (itemsToDisplay.isEmpty() && selectedTab == 1) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text("Your inventory bag is currently empty.", color = TextSecondary, fontSize = 14.sp)
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          items(itemsToDisplay) { item ->
            MarketItemCard(
              item = item,
              isSellMode = selectedTab == 1,
              canAfford = playerGold >= item.basePrice,
              onAction = {
                if (selectedTab == 0) onBuyItem(item) else onSellItem(item)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MarketItemCard(
  item: GameItem,
  isSellMode: Boolean,
  canAfford: Boolean,
  onAction: () -> Unit
) {
  val price = if (isSellMode) (item.basePrice * 0.7f).toInt() else item.basePrice

  Card(
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(text = item.iconEmoji, fontSize = 28.sp)
          Column {
            Text(text = item.name, color = TextGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = item.maranaoName, color = MaranaoGoldLight, fontSize = 12.sp)
          }
        }

        Text(
          text = "$price 💰",
          color = if (canAfford || isSellMode) MaranaoGoldLight else Color(0xFFE57373),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        text = item.description,
        color = TextPrimary,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      // Cultural Lore snippet
      Surface(
        color = SurfaceContainerDark,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "Heritage: ${item.culturalLore}",
          color = TextSecondary,
          fontSize = 11.sp,
          lineHeight = 15.sp,
          modifier = Modifier.padding(8.dp)
        )
      }

      // Action button
      Button(
        onClick = onAction,
        enabled = if (isSellMode) true else canAfford,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isSellMode) RoyalCrimson else MindanaoGreen,
          contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag(if (isSellMode) "btn_sell_${item.id}" else "btn_buy_${item.id}")
      ) {
        Text(
          text = if (isSellMode) "Sell for $price Dirhams" else "Purchase for $price Dirhams",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
