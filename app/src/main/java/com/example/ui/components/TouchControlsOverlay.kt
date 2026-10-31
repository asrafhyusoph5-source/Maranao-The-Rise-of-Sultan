package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.InteractableTarget
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun TouchControlsOverlay(
  onJoystickMove: (Float, Float) -> Unit,
  onRunToggle: (Boolean) -> Unit,
  isRunning: Boolean,
  interactableTarget: InteractableTarget?,
  onInteractClick: () -> Unit,
  onOpenJournal: () -> Unit,
  onOpenInventory: () -> Unit,
  onOpenMap: () -> Unit,
  onOpenKingdom: () -> Unit,
  onOpenSettings: () -> Unit,
  playerRankTitle: String,
  goldAmount: Int,
  trustPercent: Int,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {

    // --- TOP HUD BAR ---
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Player Rank Badge & Community Trust
      Surface(
        color = SurfaceDark.copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaranaoGold.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .background(RoyalCrimson, CircleShape)
              .border(1.dp, MaranaoGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Rank",
              tint = MaranaoGold,
              modifier = Modifier.size(16.dp)
            )
          }

          Column {
            Text(
              text = playerRankTitle,
              color = TextGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Trust: $trustPercent%",
                color = TextSecondary,
                fontSize = 11.sp
              )
              Text(
                text = "💰 $goldAmount Dirhams",
                color = MaranaoGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Right: Quick Action Navigation Buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onOpenJournal,
          modifier = Modifier
            .size(40.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            .border(1.dp, MaranaoGold.copy(alpha = 0.4f), CircleShape)
            .testTag("btn_quest_journal")
        ) {
          Icon(
            imageVector = Icons.Default.AutoStories,
            contentDescription = "Quest Journal",
            tint = TextGold,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onOpenInventory,
          modifier = Modifier
            .size(40.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            .border(1.dp, MaranaoGold.copy(alpha = 0.4f), CircleShape)
            .testTag("btn_inventory")
        ) {
          Icon(
            imageVector = Icons.Default.Backpack,
            contentDescription = "Inventory",
            tint = TextGold,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onOpenMap,
          modifier = Modifier
            .size(40.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            .border(1.dp, MaranaoGold.copy(alpha = 0.4f), CircleShape)
            .testTag("btn_world_map")
        ) {
          Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "Lake Lanao Map",
            tint = TextGold,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onOpenKingdom,
          modifier = Modifier
            .size(40.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            .border(1.dp, MaranaoGold.copy(alpha = 0.4f), CircleShape)
            .testTag("btn_kingdom_management")
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = "Kingdom & Council",
            tint = TextGold,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier
            .size(40.dp)
            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            .border(1.dp, MaranaoGold.copy(alpha = 0.4f), CircleShape)
            .testTag("btn_settings")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = TextGold,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // --- BOTTOM INTERACTION & MOVEMENT CONTROLS ---
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      // LEFT: VIRTUAL ANALOG JOYSTICK
      VirtualJoystick(
        onMove = onJoystickMove,
        modifier = Modifier.size(130.dp)
      )

      // RIGHT: ACTION BUTTONS (Sprint & Contextual Interact)
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Sprint / Run Button
        FilledTonalButton(
          onClick = { onRunToggle(!isRunning) },
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isRunning) MaranaoGold else SurfaceDark.copy(alpha = 0.8f),
            contentColor = if (isRunning) ToroganWoodDark else TextPrimary
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .height(44.dp)
            .border(1.dp, MaranaoGold, RoundedCornerShape(24.dp))
            .testTag("btn_run_toggle")
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsRun,
            contentDescription = "Sprint",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (isRunning) "Running" else "Walk", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        // Contextual Interact Button (Glows when near NPC or Torogan)
        val hasTarget = interactableTarget != null
        val targetLabel = when (interactableTarget) {
          is InteractableTarget.NpcTarget -> "Talk (${interactableTarget.npc.name})"
          is InteractableTarget.BuildingTarget -> "Inspect (${interactableTarget.building.name.take(12)}..)"
          null -> "Interact"
        }

        Button(
          onClick = onInteractClick,
          enabled = hasTarget,
          colors = ButtonDefaults.buttonColors(
            containerColor = RoyalCrimson,
            contentColor = MaranaoGoldLight,
            disabledContainerColor = SurfaceDark.copy(alpha = 0.5f),
            disabledContentColor = TextSecondary.copy(alpha = 0.4f)
          ),
          shape = RoundedCornerShape(28.dp),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
          modifier = Modifier
            .height(56.dp)
            .border(
              width = if (hasTarget) 2.dp else 1.dp,
              color = if (hasTarget) MaranaoGold else SurfaceCardBorder,
              shape = RoundedCornerShape(28.dp)
            )
            .testTag("btn_interact")
        ) {
          Icon(
            imageVector = if (interactableTarget is InteractableTarget.NpcTarget) Icons.Default.Chat else Icons.Default.TouchApp,
            contentDescription = "Interact",
            tint = if (hasTarget) MaranaoGold else TextSecondary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = targetLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun VirtualJoystick(
  onMove: (Float, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var thumbOffset by remember { mutableStateOf(Offset.Zero) }
  val maxRadius = 55f

  Box(
    modifier = modifier
      .background(SurfaceDark.copy(alpha = 0.75f), CircleShape)
      .border(2.dp, MaranaoGold.copy(alpha = 0.6f), CircleShape)
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            val center = Offset(size.width / 2f, size.height / 2f)
            val diff = offset - center
            val dist = diff.getDistance()
            val clamped = if (dist > maxRadius) diff * (maxRadius / dist) else diff
            thumbOffset = clamped
            onMove(clamped.x / maxRadius, clamped.y / maxRadius)
          },
          onDrag = { change, dragAmount ->
            change.consume()
            val newOffset = thumbOffset + dragAmount
            val dist = newOffset.getDistance()
            val clamped = if (dist > maxRadius) newOffset * (maxRadius / dist) else newOffset
            thumbOffset = clamped
            onMove(clamped.x / maxRadius, clamped.y / maxRadius)
          },
          onDragEnd = {
            thumbOffset = Offset.Zero
            onMove(0f, 0f)
          },
          onDragCancel = {
            thumbOffset = Offset.Zero
            onMove(0f, 0f)
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    // Inner center marker
    Box(
      modifier = Modifier
        .size(16.dp)
        .background(MaranaoGold.copy(alpha = 0.3f), CircleShape)
    )

    // Moving thumb stick
    Box(
      modifier = Modifier
        .offset { IntOffset(thumbOffset.x.roundToInt(), thumbOffset.y.roundToInt()) }
        .size(52.dp)
        .background(
          brush = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(MaranaoGoldLight, RoyalCrimson)
          ),
          shape = CircleShape
        )
        .border(2.dp, MaranaoGold, CircleShape)
    )
  }
}
