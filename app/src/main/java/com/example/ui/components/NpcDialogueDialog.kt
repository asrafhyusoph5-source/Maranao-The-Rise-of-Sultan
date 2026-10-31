package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DialogueChoice
import com.example.model.NPC
import com.example.ui.theme.*

@Composable
fun NpcDialogueDialog(
  npc: NPC,
  onDismiss: () -> Unit,
  onSelectChoice: (DialogueChoice) -> Unit,
  onOpenShop: () -> Unit
) {
  var selectedResponse by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = SurfaceDark,
      border = androidx.compose.foundation.BorderStroke(2.dp, MaranaoGold),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // TOP HEADER: NPC AVATAR, NAME & CLOSE BUTTON
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(Color(npc.primaryColorHex), CircleShape)
                .border(2.dp, MaranaoGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = npc.name.take(1),
                color = MaranaoGoldLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Column {
              Text(
                text = npc.name,
                color = TextGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = npc.maranaoTitle,
                color = MaranaoGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = npc.role,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_dialog")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CULTURAL DIALOGUE BODY
        Surface(
          color = SurfaceContainerDark,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "\"${npc.dialogNode.greeting}\"",
              color = TextPrimary,
              fontSize = 14.sp,
              lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = npc.dialogNode.culturalWisdom,
              color = TextGold.copy(alpha = 0.9f),
              fontSize = 13.sp,
              lineHeight = 18.sp,
              fontWeight = FontWeight.Normal
            )

            if (selectedResponse != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                color = RoyalCrimsonDark.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalCrimsonLight)
              ) {
                Text(
                  text = selectedResponse ?: "",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  lineHeight = 18.sp,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // DIALOGUE CHOICES
        Text(
          text = "Your Response:",
          color = TextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          npc.dialogNode.choices.forEachIndexed { index, choice ->
            OutlinedButton(
              onClick = {
                selectedResponse = choice.responseText
                onSelectChoice(choice)
              },
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = SurfaceCard,
                contentColor = TextGold
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("dialog_choice_$index")
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = choice.label,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.weight(1f)
                )
                if (choice.wisdomReward > 0 || choice.trustReward > 0) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = "Reward",
                      tint = MaranaoGold,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = "+${choice.wisdomReward + choice.trustReward}",
                      color = MaranaoGoldLight,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }

          // Merchant quick button
          if (npc.isMerchant) {
            Button(
              onClick = onOpenShop,
              colors = ButtonDefaults.buttonColors(containerColor = MindanaoGreen, contentColor = TextPrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_trade_with_npc")
            ) {
              Text(
                text = "Trade & Browse Wares with ${npc.name}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
