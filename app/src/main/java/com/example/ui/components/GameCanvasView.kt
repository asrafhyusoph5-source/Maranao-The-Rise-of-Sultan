package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.engine.*
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun GameCanvasView(
  worldEngine: WorldEngine,
  playerCustomization: PlayerCustomization,
  onInteractWith: (InteractableTarget) -> Unit,
  modifier: Modifier = Modifier
) {
  // Drag on right half of canvas controls camera orbit
  Canvas(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          // Adjust camera yaw and pitch
          worldEngine.camera.yawDegrees = (worldEngine.camera.yawDegrees - dragAmount.x * 0.35f + 360f) % 360f
          worldEngine.camera.pitchDegrees = (worldEngine.camera.pitchDegrees + dragAmount.y * 0.25f).coerceIn(12f, 75f)
        }
      }
  ) {
    val width = size.width
    val height = size.height
    val cam = worldEngine.camera

    // 1. SKY & MOUNTAIN BACKGROUND
    drawSkyAndMountains(this, width, height, cam.yawDegrees, worldEngine.timeOfDayHours)

    // 2. PROJECTED ITEMS LIST (Painter's algorithm sorting by camera depth)
    val renderQueue = mutableListOf<ProjectedRenderItem>()

    // A. Lake Lanao Water Surface (Z from -9 down to -65)
    val lakeCenter = Vector3(0f, -0.3f, -32f)
    val lakeProj = cam.projectToScreen(lakeCenter, width, height)
    if (lakeProj != null) {
      renderQueue.add(
        ProjectedRenderItem(lakeProj.third) { scope, w, h ->
          drawLakeLanao(scope, w, h, cam, worldEngine.waterWaveTimer, worldEngine.timeOfDayHours)
        }
      )
    }

    // B. Village Ground & Paths
    val groundCenter = Vector3(0f, -0.05f, 10f)
    val groundProj = cam.projectToScreen(groundCenter, width, height)
    if (groundProj != null) {
      renderQueue.add(
        ProjectedRenderItem(groundProj.third + 5f) { scope, w, h ->
          drawVillageGround(scope, w, h, cam)
        }
      )
    }

    // C. Buildings & Architecture
    for (building in worldEngine.buildings) {
      val bProj = cam.projectToScreen(building.position, width, height)
      if (bProj != null) {
        renderQueue.add(
          ProjectedRenderItem(bProj.third) { scope, _, _ ->
            drawBuilding3D(scope, cam, building, width, height)
          }
        )
      }
    }

    // D. Palm Trees
    for (treePos in worldEngine.palmTreePositions) {
      val tProj = cam.projectToScreen(treePos, width, height)
      if (tProj != null) {
        renderQueue.add(
          ProjectedRenderItem(tProj.third) { scope, _, _ ->
            drawPalmTree(scope, cam, treePos, width, height)
          }
        )
      }
    }

    // E. NPCs
    for (npc in NPCDatabase.villagers) {
      val npcPos = Vector3(npc.worldX, 0f, npc.worldZ)
      val nProj = cam.projectToScreen(npcPos, width, height)
      if (nProj != null) {
        renderQueue.add(
          ProjectedRenderItem(nProj.third) { scope, _, _ ->
            drawNPCCharacter(scope, cam, npc, nProj.first, nProj.second, nProj.third)
          }
        )
      }
    }

    // F. Player Character
    val playerProj = cam.projectToScreen(worldEngine.playerPosition, width, height)
    if (playerProj != null) {
      renderQueue.add(
        ProjectedRenderItem(playerProj.third) { scope, _, _ ->
          drawPlayerCharacter(
            scope,
            cam,
            worldEngine.playerPosition,
            worldEngine.playerFacingDegrees,
            worldEngine.walkCycleTimer,
            worldEngine.isMoving,
            playerCustomization,
            playerProj.first,
            playerProj.second,
            playerProj.third
          )
        }
      )
    }

    // 3. EXECUTE DRAWING IN DEPTH ORDER (Furthest to closest)
    renderQueue.sortByDescending { it.depth }
    for (item in renderQueue) {
      item.renderBlock(this, width, height)
    }

    // 4. FLOATING INTERACTION INDICATOR (if near an entity)
    val nearest = worldEngine.findNearestInteractable()
    if (nearest != null) {
      drawInteractionPrompt(this, cam, nearest, width, height)
    }
  }
}

// Draw sky gradient, distant misty mountains, and sun/moon
private fun drawSkyAndMountains(
  scope: DrawScope,
  width: Float,
  height: Float,
  cameraYaw: Float,
  timeHours: Float
) {
  // Sunset / golden hour gradient
  val skyTop = Color(0xFF2C3E50)
  val skyMid = Color(0xFFD4621E)
  val skyBottom = Color(0xFFF7CF68)

  val skyBrush = Brush.verticalGradient(
    colors = listOf(skyTop, skyMid, skyBottom),
    startY = 0f,
    endY = height * 0.65f
  )
  scope.drawRect(brush = skyBrush, size = Size(width, height * 0.65f))

  // Glowing sun setting over the lake mountains
  val sunAngle = (cameraYaw * 0.015f) % 1f
  val sunX = ((width * 0.5f) + (cameraYaw * 3.5f)) % width
  val sunY = height * 0.28f
  scope.drawCircle(
    color = Color(0x33FFDE8A),
    radius = 55f,
    center = Offset(sunX, sunY)
  )
  scope.drawCircle(
    color = Color(0xFFFFD15C),
    radius = 32f,
    center = Offset(sunX, sunY)
  )

  // Distant mountain ranges of Lake Lanao (Sacred Mt. Ragang / Piapayungan silhouettes)
  val mtnPath = Path().apply {
    moveTo(0f, height * 0.50f)
    var curX = 0f
    val step = width / 8f
    val mountainHeights = listOf(0.42f, 0.38f, 0.44f, 0.35f, 0.40f, 0.37f, 0.43f, 0.39f, 0.45f)
    for (i in 0..8) {
      val peakY = height * mountainHeights[i % mountainHeights.size]
      quadraticTo(curX + step * 0.5f, peakY, curX + step, height * 0.50f)
      curX += step
    }
    lineTo(width, height * 0.65f)
    lineTo(0f, height * 0.65f)
    close()
  }
  scope.drawPath(
    path = mtnPath,
    color = Color(0xAA1C2833)
  )
}

// Draw Lake Lanao waters with ripple lines and golden reflection
private fun drawLakeLanao(
  scope: DrawScope,
  width: Float,
  height: Float,
  cam: Camera3D,
  waveTimer: Float,
  timeHours: Float
) {
  // Project corners of Lake Lanao
  val nw = cam.projectToScreen(Vector3(-45f, -0.3f, -55f), width, height) ?: return
  val ne = cam.projectToScreen(Vector3(45f, -0.3f, -55f), width, height) ?: return
  val se = cam.projectToScreen(Vector3(45f, -0.3f, -10f), width, height) ?: return
  val sw = cam.projectToScreen(Vector3(-45f, -0.3f, -10f), width, height) ?: return

  val waterPath = Path().apply {
    moveTo(nw.first, nw.second)
    lineTo(ne.first, ne.second)
    lineTo(se.first, se.second)
    lineTo(sw.first, sw.second)
    close()
  }

  // Deep Lake gradient
  val waterBrush = Brush.verticalGradient(
    colors = listOf(LakeLanaoDeep, LakeLanaoBlue, LakeLanaoSurface),
    startY = nw.second,
    endY = sw.second
  )
  scope.drawPath(path = waterPath, brush = waterBrush)

  // Animated wave lines
  for (i in 1..7) {
    val z = -12f - (i * 6f)
    val waveOffset = sin(waveTimer + i * 1.2f) * 0.3f
    val p1 = cam.projectToScreen(Vector3(-25f, -0.3f + waveOffset, z), width, height)
    val p2 = cam.projectToScreen(Vector3(25f, -0.3f + waveOffset, z), width, height)
    if (p1 != null && p2 != null) {
      scope.drawLine(
        color = Color(0x44FFFFFF),
        start = Offset(p1.first, p1.second),
        end = Offset(p2.first, p2.second),
        strokeWidth = 2.5f
      )
    }
  }

  // Golden shimmer streak across the lake
  val midTop = cam.projectToScreen(Vector3(0f, -0.3f, -48f), width, height)
  val midBot = cam.projectToScreen(Vector3(0f, -0.3f, -11f), width, height)
  if (midTop != null && midBot != null) {
    val shimmerBrush = Brush.linearGradient(
      colors = listOf(Color(0x00FFD573), Color(0x66FFD573), Color(0x00FFD573)),
      start = Offset(midTop.first - 30f, midTop.second),
      end = Offset(midTop.first + 30f, midTop.second)
    )
    val shimmerPath = Path().apply {
      moveTo(midTop.first - 15f, midTop.second)
      lineTo(midTop.first + 15f, midTop.second)
      lineTo(midBot.first + 45f, midBot.second)
      lineTo(midBot.first - 45f, midBot.second)
      close()
    }
    scope.drawPath(path = shimmerPath, brush = shimmerBrush)
  }
}

// Draw village soil, paths and lake shore
private fun drawVillageGround(
  scope: DrawScope,
  width: Float,
  height: Float,
  cam: Camera3D
) {
  // Ground corners
  val nw = cam.projectToScreen(Vector3(-35f, 0f, 36f), width, height) ?: return
  val ne = cam.projectToScreen(Vector3(35f, 0f, 36f), width, height) ?: return
  val se = cam.projectToScreen(Vector3(35f, 0f, -10f), width, height) ?: return
  val sw = cam.projectToScreen(Vector3(-35f, 0f, -10f), width, height) ?: return

  val groundPath = Path().apply {
    moveTo(nw.first, nw.second)
    lineTo(ne.first, ne.second)
    lineTo(se.first, se.second)
    lineTo(sw.first, sw.second)
    close()
  }

  // Lush grassy earth
  scope.drawPath(path = groundPath, color = Color(0xFF233B26))

  // Village central dirt road leading from Lake Docks to Torogan
  val roadTopW = cam.projectToScreen(Vector3(-3.0f, 0.02f, 22f), width, height)
  val roadTopE = cam.projectToScreen(Vector3(3.0f, 0.02f, 22f), width, height)
  val roadBotE = cam.projectToScreen(Vector3(3.8f, 0.02f, -10f), width, height)
  val roadBotW = cam.projectToScreen(Vector3(-3.8f, 0.02f, -10f), width, height)

  if (roadTopW != null && roadTopE != null && roadBotE != null && roadBotW != null) {
    val roadPath = Path().apply {
      moveTo(roadTopW.first, roadTopW.second)
      lineTo(roadTopE.first, roadTopE.second)
      lineTo(roadBotE.first, roadBotE.second)
      lineTo(roadBotW.first, roadBotW.second)
      close()
    }
    scope.drawPath(path = roadPath, color = Color(0xFF6B5842))
  }

  // Cross path connecting Weaving Pavilion and Forge
  val crossW = cam.projectToScreen(Vector3(-14f, 0.02f, 4f), width, height)
  val crossE = cam.projectToScreen(Vector3(14f, 0.02f, 4f), width, height)
  if (crossW != null && crossE != null) {
    scope.drawLine(
      color = Color(0xFF5E4D3B),
      start = Offset(crossW.first, crossW.second),
      end = Offset(crossE.first, crossE.second),
      strokeWidth = 24f * (width / (cam.distance * 80f)).coerceIn(8f, 40f)
    )
  }
}

// 3D Architectural Renderer: Torogan with Panolong, Stilt Houses, Awang Boats, Docks, Forge, Weaving Pavilion
private fun drawBuilding3D(
  scope: DrawScope,
  cam: Camera3D,
  b: WorldBuilding,
  screenWidth: Float,
  screenHeight: Float
) {
  val basePos = b.position
  val hw = b.width / 2f
  val hd = b.depth / 2f
  val h = b.height

  when (b.type) {
    WorldObjectType.TOROGAN_PALACE -> {
      // Grand Torogan Palace:
      // 1. Massive log stilts
      val stiltH = 1.4f
      val floorY = stiltH
      val roofBaseY = floorY + 2.2f
      val roofPeakY = roofBaseY + 2.4f

      // 4 Stilt posts
      val stiltCoords = listOf(
        Vector3(basePos.x - hw * 0.8f, 0f, basePos.z - hd * 0.8f),
        Vector3(basePos.x + hw * 0.8f, 0f, basePos.z - hd * 0.8f),
        Vector3(basePos.x - hw * 0.8f, 0f, basePos.z + hd * 0.8f),
        Vector3(basePos.x + hw * 0.8f, 0f, basePos.z + hd * 0.8f)
      )
      for (sc in stiltCoords) {
        val pBot = cam.projectToScreen(sc, screenWidth, screenHeight)
        val pTop = cam.projectToScreen(Vector3(sc.x, floorY, sc.z), screenWidth, screenHeight)
        if (pBot != null && pTop != null) {
          scope.drawLine(
            color = ToroganWoodDark,
            start = Offset(pBot.first, pBot.second),
            end = Offset(pTop.first, pTop.second),
            strokeWidth = 14f * (screenWidth / (pBot.third * 60f)).coerceIn(4f, 22f)
          )
        }
      }

      // Main Living Hall Walls
      drawBox(
        scope, cam,
        Vector3(basePos.x, floorY, basePos.z),
        hw * 1.8f, 2.0f, hd * 1.8f,
        screenWidth, screenHeight,
        ToroganWoodMid, ToroganWoodDark
      )

      // Steep high-pitched Torogan roof with overhanging gables
      val rNW = cam.projectToScreen(Vector3(basePos.x - hw * 1.15f, roofBaseY, basePos.z + hd * 1.15f), screenWidth, screenHeight)
      val rNE = cam.projectToScreen(Vector3(basePos.x + hw * 1.15f, roofBaseY, basePos.z + hd * 1.15f), screenWidth, screenHeight)
      val rSE = cam.projectToScreen(Vector3(basePos.x + hw * 1.15f, roofBaseY, basePos.z - hd * 1.15f), screenWidth, screenHeight)
      val rSW = cam.projectToScreen(Vector3(basePos.x - hw * 1.15f, roofBaseY, basePos.z - hd * 1.15f), screenWidth, screenHeight)
      val rRidgeW = cam.projectToScreen(Vector3(basePos.x - hw * 0.8f, roofPeakY, basePos.z), screenWidth, screenHeight)
      val rRidgeE = cam.projectToScreen(Vector3(basePos.x + hw * 0.8f, roofPeakY, basePos.z), screenWidth, screenHeight)

      if (rNW != null && rNE != null && rSE != null && rSW != null && rRidgeW != null && rRidgeE != null) {
        // Front roof slope
        val frontRoof = Path().apply {
          moveTo(rSW.first, rSW.second)
          lineTo(rSE.first, rSE.second)
          lineTo(rRidgeE.first, rRidgeE.second)
          lineTo(rRidgeW.first, rRidgeW.second)
          close()
        }
        scope.drawPath(frontRoof, color = Color(0xFF6B4832))

        // Side roof gable
        val sideRoof = Path().apply {
          moveTo(rSW.first, rSW.second)
          lineTo(rNW.first, rNW.second)
          lineTo(rRidgeW.first, rRidgeW.second)
          close()
        }
        scope.drawPath(sideRoof, color = Color(0xFF4A3222))
      }

      // PANOLONG: Flaring wing-like beams carved with Okir motifs!
      // Extending out to the sides and front of the Torogan floor
      val panolongLeftTip = cam.projectToScreen(Vector3(basePos.x - hw * 1.35f, floorY + 0.35f, basePos.z - hd * 0.9f), screenWidth, screenHeight)
      val panolongLeftBase = cam.projectToScreen(Vector3(basePos.x - hw * 0.9f, floorY, basePos.z - hd * 0.9f), screenWidth, screenHeight)
      val panolongRightTip = cam.projectToScreen(Vector3(basePos.x + hw * 1.35f, floorY + 0.35f, basePos.z - hd * 0.9f), screenWidth, screenHeight)
      val panolongRightBase = cam.projectToScreen(Vector3(basePos.x + hw * 0.9f, floorY, basePos.z - hd * 0.9f), screenWidth, screenHeight)

      if (panolongLeftTip != null && panolongLeftBase != null) {
        // Golden Okir carved beam wing
        val panPath = Path().apply {
          moveTo(panolongLeftBase.first, panolongLeftBase.second)
          quadraticTo(
            (panolongLeftBase.first + panolongLeftTip.first) / 2f,
            panolongLeftBase.second - 15f,
            panolongLeftTip.first,
            panolongLeftTip.second
          )
          lineTo(panolongLeftTip.first + 6f, panolongLeftTip.second + 10f)
          close()
        }
        scope.drawPath(panPath, color = MaranaoGold)
        scope.drawPath(panPath, color = RoyalCrimson, style = Stroke(width = 2.5f))
      }

      if (panolongRightTip != null && panolongRightBase != null) {
        val panPath = Path().apply {
          moveTo(panolongRightBase.first, panolongRightBase.second)
          quadraticTo(
            (panolongRightBase.first + panolongRightTip.first) / 2f,
            panolongRightBase.second - 15f,
            panolongRightTip.first,
            panolongRightTip.second
          )
          lineTo(panolongRightTip.first - 6f, panolongRightTip.second + 10f)
          close()
        }
        scope.drawPath(panPath, color = MaranaoGold)
        scope.drawPath(panPath, color = RoyalCrimson, style = Stroke(width = 2.5f))
      }
    }

    WorldObjectType.WEAVING_PAVILION -> {
      // Elevated open bamboo workshop with hanging colorful Malong textiles
      drawBox(
        scope, cam,
        basePos,
        b.width, b.height * 0.4f, b.depth,
        screenWidth, screenHeight,
        Color(0xFF5A4434), Color(0xFF3E2D22)
      )
      // Hanging textile banner (Malong Landap with langkit)
      val bTop = cam.projectToScreen(Vector3(basePos.x - 1.2f, b.height * 0.7f, basePos.z - hd), screenWidth, screenHeight)
      val bBot = cam.projectToScreen(Vector3(basePos.x + 1.2f, b.height * 0.2f, basePos.z - hd), screenWidth, screenHeight)
      if (bTop != null && bBot != null) {
        scope.drawRect(
          color = RoyalCrimson,
          topLeft = Offset(bTop.first, bTop.second),
          size = Size(abs(bBot.first - bTop.first).coerceAtLeast(18f), abs(bBot.second - bTop.second).coerceAtLeast(30f))
        )
        // Center golden Langkit stripe
        scope.drawLine(
          color = MaranaoGold,
          start = Offset((bTop.first + bBot.first) / 2f, bTop.second),
          end = Offset((bTop.first + bBot.first) / 2f, bBot.second),
          strokeWidth = 5f
        )
      }
    }

    WorldObjectType.ARTISAN_FORGE -> {
      // Forge with anvil and Kulintang rack
      drawBox(
        scope, cam,
        basePos,
        b.width, b.height * 0.6f, b.depth,
        screenWidth, screenHeight,
        Color(0xFF6B5344), Color(0xFF4A382C)
      )
      // Kulintang gongs on stand in front
      val gPos = cam.projectToScreen(Vector3(basePos.x, 0.4f, basePos.z - hd - 0.5f), screenWidth, screenHeight)
      if (gPos != null) {
        // Draw row of golden gong circles
        for (gi in -3..3) {
          scope.drawCircle(
            color = MaranaoGold,
            radius = 6f,
            center = Offset(gPos.first + (gi * 11f), gPos.second)
          )
        }
      }
    }

    WorldObjectType.LAKE_DOCK -> {
      // Hardwood boardwalk out into the water
      val pNW = cam.projectToScreen(Vector3(basePos.x - hw, 0f, basePos.z + hd), screenWidth, screenHeight)
      val pNE = cam.projectToScreen(Vector3(basePos.x + hw, 0f, basePos.z + hd), screenWidth, screenHeight)
      val pSE = cam.projectToScreen(Vector3(basePos.x + hw, 0f, basePos.z - hd), screenWidth, screenHeight)
      val pSW = cam.projectToScreen(Vector3(basePos.x - hw, 0f, basePos.z - hd), screenWidth, screenHeight)

      if (pNW != null && pNE != null && pSE != null && pSW != null) {
        val dockPath = Path().apply {
          moveTo(pNW.first, pNW.second)
          lineTo(pNE.first, pNE.second)
          lineTo(pSE.first, pSE.second)
          lineTo(pSW.first, pSW.second)
          close()
        }
        scope.drawPath(dockPath, color = Color(0xFF634832))
        scope.drawPath(dockPath, color = Color(0xFF3B281B), style = Stroke(width = 2.5f))
      }
    }

    WorldObjectType.AWANG_BOAT -> {
      // Traditional Maranao Awang Canoe with outrigger booms
      val bBow = cam.projectToScreen(Vector3(basePos.x, 0f, basePos.z - hd), screenWidth, screenHeight)
      val bStern = cam.projectToScreen(Vector3(basePos.x, 0f, basePos.z + hd), screenWidth, screenHeight)
      val outrigL = cam.projectToScreen(Vector3(basePos.x - 1.4f, -0.05f, basePos.z), screenWidth, screenHeight)
      val outrigR = cam.projectToScreen(Vector3(basePos.x + 1.4f, -0.05f, basePos.z), screenWidth, screenHeight)

      if (bBow != null && bStern != null) {
        // Slender curved canoe hull
        val boatPath = Path().apply {
          moveTo(bBow.first, bBow.second)
          quadraticTo(
            (bBow.first + bStern.first) / 2f + 12f,
            (bBow.second + bStern.second) / 2f,
            bStern.first,
            bStern.second
          )
          quadraticTo(
            (bBow.first + bStern.first) / 2f - 12f,
            (bBow.second + bStern.second) / 2f,
            bBow.first,
            bBow.second
          )
          close()
        }
        scope.drawPath(boatPath, color = Color(b.primaryColor))
        scope.drawPath(boatPath, color = Color(b.accentColor), style = Stroke(width = 2f))

        // Bamboo outriggers
        if (outrigL != null && outrigR != null) {
          val midHull = Offset((bBow.first + bStern.first) / 2f, (bBow.second + bStern.second) / 2f)
          scope.drawLine(color = Color(0xFFD4C18F), start = midHull, end = Offset(outrigL.first, outrigL.second), strokeWidth = 3f)
          scope.drawLine(color = Color(0xFFD4C18F), start = midHull, end = Offset(outrigR.first, outrigR.second), strokeWidth = 3f)
        }
      }
    }

    WorldObjectType.VILLAGE_HOUSE -> {
      // Traditional stilt house
      drawBox(
        scope, cam,
        Vector3(basePos.x, 0.8f, basePos.z),
        b.width, b.height * 0.7f, b.depth,
        screenWidth, screenHeight,
        Color(b.primaryColor), Color(0xFF3A291E)
      )
    }

    WorldObjectType.MARKET_STALL -> {
      // Market canopy
      drawBox(
        scope, cam,
        basePos,
        b.width, b.height * 0.5f, b.depth,
        screenWidth, screenHeight,
        Color(b.primaryColor), Color(b.accentColor)
      )
    }

    WorldObjectType.MOSQUE_PORTAL -> {
      // Arched gateway and prayer courtyard
      drawBox(
        scope, cam,
        basePos,
        b.width, b.height * 0.8f, b.depth,
        screenWidth, screenHeight,
        Color(0xFFE8E5DC), Color(0xFF2E855C)
      )
    }

    else -> {}
  }
}

// Helper to draw a shaded 3D prism box
private fun drawBox(
  scope: DrawScope,
  cam: Camera3D,
  center: Vector3,
  width: Float,
  height: Float,
  depth: Float,
  screenWidth: Float,
  screenHeight: Float,
  frontColor: Color,
  sideColor: Color
) {
  val hw = width / 2f
  val hd = depth / 2f

  val p1 = cam.projectToScreen(Vector3(center.x - hw, center.y, center.z - hd), screenWidth, screenHeight)
  val p2 = cam.projectToScreen(Vector3(center.x + hw, center.y, center.z - hd), screenWidth, screenHeight)
  val p3 = cam.projectToScreen(Vector3(center.x + hw, center.y + height, center.z - hd), screenWidth, screenHeight)
  val p4 = cam.projectToScreen(Vector3(center.x - hw, center.y + height, center.z - hd), screenWidth, screenHeight)

  if (p1 != null && p2 != null && p3 != null && p4 != null) {
    val frontPath = Path().apply {
      moveTo(p1.first, p1.second)
      lineTo(p2.first, p2.second)
      lineTo(p3.first, p3.second)
      lineTo(p4.first, p4.second)
      close()
    }
    scope.drawPath(frontPath, color = frontColor)
    scope.drawPath(frontPath, color = sideColor, style = Stroke(width = 1.5f))
  }
}

// Draw tropical palm tree with lush fronds
private fun drawPalmTree(
  scope: DrawScope,
  cam: Camera3D,
  pos: Vector3,
  screenWidth: Float,
  screenHeight: Float
) {
  val base = cam.projectToScreen(pos, screenWidth, screenHeight) ?: return
  val top = cam.projectToScreen(Vector3(pos.x, 3.8f, pos.z), screenWidth, screenHeight) ?: return

  // Trunk
  scope.drawLine(
    color = Color(0xFF5E4935),
    start = Offset(base.first, base.second),
    end = Offset(top.first, top.second),
    strokeWidth = 6f
  )

  // Palm fronds radiating out
  val frondAngles = listOf(0, 45, 90, 135, 180, 225, 270, 315)
  for (ang in frondAngles) {
    val rad = Math.toRadians(ang.toDouble())
    val fx = top.first + (cos(rad) * 26f).toFloat()
    val fy = top.second + (sin(rad) * 16f).toFloat() + 8f
    scope.drawLine(
      color = MindanaoGreen,
      start = Offset(top.first, top.second),
      end = Offset(fx, fy),
      strokeWidth = 4f
    )
  }
}

// Draw authentic NPC character with cultural clothing and title tag
private fun drawNPCCharacter(
  scope: DrawScope,
  cam: Camera3D,
  npc: NPC,
  screenX: Float,
  screenY: Float,
  depth: Float
) {
  val scale = (140f / depth).coerceIn(16f, 65f)

  // Character shadow
  scope.drawOval(
    color = Color(0x55000000),
    topLeft = Offset(screenX - scale * 0.45f, screenY - scale * 0.15f),
    size = Size(scale * 0.9f, scale * 0.3f)
  )

  // Character body & attire (Malong / tunic)
  val bodyColor = Color(npc.primaryColorHex)
  val bodyTop = screenY - scale * 1.3f

  // Robe / Malong
  val robePath = Path().apply {
    moveTo(screenX - scale * 0.32f, screenY - scale * 0.2f)
    lineTo(screenX + scale * 0.32f, screenY - scale * 0.2f)
    lineTo(screenX + scale * 0.22f, bodyTop + scale * 0.45f)
    lineTo(screenX - scale * 0.22f, bodyTop + scale * 0.45f)
    close()
  }
  scope.drawPath(robePath, color = bodyColor)

  // Golden accent band (Langkit)
  scope.drawLine(
    color = MaranaoGold,
    start = Offset(screenX, bodyTop + scale * 0.45f),
    end = Offset(screenX, screenY - scale * 0.2f),
    strokeWidth = 3f
  )

  // Head & Face
  scope.drawCircle(
    color = Color(0xFFC68642), // Warm Filipino skin tone
    radius = scale * 0.22f,
    center = Offset(screenX, bodyTop + scale * 0.22f)
  )

  // Headwear: Tubao or Kombong
  scope.drawArc(
    color = MaranaoGold,
    startAngle = 180f,
    sweepAngle = 180f,
    useCenter = true,
    topLeft = Offset(screenX - scale * 0.24f, bodyTop),
    size = Size(scale * 0.48f, scale * 0.28f)
  )

  // Hovering Name Badge & Quest Marker
  val badgeY = bodyTop - 26f
  val hasQuest = npc.questIdOffered != null
  if (hasQuest) {
    // Quest icon "!"
    scope.drawCircle(
      color = MaranaoGold,
      radius = 11f,
      center = Offset(screenX, badgeY - 18f)
    )
    scope.drawCircle(
      color = RoyalCrimson,
      radius = 3.5f,
      center = Offset(screenX, badgeY - 18f)
    )
  }

  // Name pill
  val pillW = 74f
  val pillH = 18f
  scope.drawRoundRect(
    color = Color(0xCC1A1614),
    topLeft = Offset(screenX - pillW / 2f, badgeY),
    size = Size(pillW, pillH),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
  )
}

// Draw Player Character with customizable traditional clothing and walk cycle
private fun drawPlayerCharacter(
  scope: DrawScope,
  cam: Camera3D,
  pos: Vector3,
  facingDeg: Float,
  walkTimer: Float,
  isMoving: Boolean,
  custom: PlayerCustomization,
  screenX: Float,
  screenY: Float,
  depth: Float
) {
  val scale = (150f / depth).coerceIn(20f, 75f)

  // Walk cycle offsets
  val legSwing = if (isMoving) sin(walkTimer) * (scale * 0.22f) else 0f
  val bobY = if (isMoving) abs(sin(walkTimer * 2f)) * (scale * 0.08f) else 0f

  val baseY = screenY - bobY

  // Shadow
  scope.drawOval(
    color = Color(0x66000000),
    topLeft = Offset(screenX - scale * 0.5f, screenY - scale * 0.16f),
    size = Size(scale, scale * 0.32f)
  )

  // Skin tone
  val skinColors = listOf(
    Color(0xFFD2996E),
    Color(0xFFBA7A45),
    Color(0xFFA06332),
    Color(0xFF7F4B23)
  )
  val skin = skinColors.getOrElse(custom.skinToneIndex) { skinColors[1] }

  // Legs / Feet
  val leftLegX = screenX - scale * 0.15f
  val rightLegX = screenX + scale * 0.15f
  val legBottom = baseY - scale * 0.1f

  scope.drawLine(
    color = Color(0xFF332319),
    start = Offset(leftLegX, baseY - scale * 0.5f),
    end = Offset(leftLegX, legBottom + legSwing),
    strokeWidth = scale * 0.12f
  )
  scope.drawLine(
    color = Color(0xFF332319),
    start = Offset(rightLegX, baseY - scale * 0.5f),
    end = Offset(rightLegX, legBottom - legSwing),
    strokeWidth = scale * 0.12f
  )

  // Lower Clothing: Malong Landap
  val malongTop = baseY - scale * 0.85f
  val malongPath = Path().apply {
    moveTo(screenX - scale * 0.30f, baseY - scale * 0.25f)
    lineTo(screenX + scale * 0.30f, baseY - scale * 0.25f)
    lineTo(screenX + scale * 0.24f, malongTop)
    lineTo(screenX - scale * 0.24f, malongTop)
    close()
  }
  scope.drawPath(malongPath, color = RoyalCrimson)

  // Vertical Langkit band on Malong
  scope.drawLine(
    color = MaranaoGold,
    start = Offset(screenX, malongTop),
    end = Offset(screenX, baseY - scale * 0.25f),
    strokeWidth = scale * 0.08f
  )

  // Upper Garment: Embroidered Vest / Tunic
  val torsoTop = baseY - scale * 1.35f
  val vestPath = Path().apply {
    moveTo(screenX - scale * 0.25f, malongTop)
    lineTo(screenX + scale * 0.25f, malongTop)
    lineTo(screenX + scale * 0.28f, torsoTop)
    lineTo(screenX - scale * 0.28f, torsoTop)
    close()
  }
  scope.drawPath(vestPath, color = MaranaoGold)

  // Arms swinging
  val armSwing = if (isMoving) -sin(walkTimer) * (scale * 0.18f) else 0f
  scope.drawLine(
    color = skin,
    start = Offset(screenX - scale * 0.28f, torsoTop + scale * 0.1f),
    end = Offset(screenX - scale * 0.32f, malongTop + armSwing),
    strokeWidth = scale * 0.09f
  )
  scope.drawLine(
    color = skin,
    start = Offset(screenX + scale * 0.28f, torsoTop + scale * 0.1f),
    end = Offset(screenX + scale * 0.32f, malongTop - armSwing),
    strokeWidth = scale * 0.09f
  )

  // Head
  val headCenterY = torsoTop - scale * 0.22f
  scope.drawCircle(
    color = skin,
    radius = scale * 0.22f,
    center = Offset(screenX, headCenterY)
  )

  // Headwear: Tubao headband or Kombong
  if (custom.headwear.contains("Tubao", ignoreCase = true)) {
    // Red and gold wrapped crown
    scope.drawArc(
      color = RoyalCrimson,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(screenX - scale * 0.24f, headCenterY - scale * 0.24f),
      size = Size(scale * 0.48f, scale * 0.3f),
      style = Stroke(width = scale * 0.12f)
    )
  } else if (custom.headwear.contains("Kombong") || custom.headwear.contains("Hijab")) {
    scope.drawArc(
      color = MindanaoGreen,
      startAngle = 140f,
      sweepAngle = 260f,
      useCenter = true,
      topLeft = Offset(screenX - scale * 0.28f, headCenterY - scale * 0.28f),
      size = Size(scale * 0.56f, scale * 0.56f)
    )
  }
}

// Draw Floating interaction badge over nearest interactable target
private fun drawInteractionPrompt(
  scope: DrawScope,
  cam: Camera3D,
  target: InteractableTarget,
  screenWidth: Float,
  screenHeight: Float
) {
  val worldPos = when (target) {
    is InteractableTarget.NpcTarget -> Vector3(target.npc.worldX, 1.8f, target.npc.worldZ)
    is InteractableTarget.BuildingTarget -> Vector3(target.building.position.x, target.building.height + 0.6f, target.building.position.z)
  }

  val proj = cam.projectToScreen(worldPos, screenWidth, screenHeight) ?: return

  val promptText = when (target) {
    is InteractableTarget.NpcTarget -> "Talk with ${target.npc.name}"
    is InteractableTarget.BuildingTarget -> "Inspect ${target.building.name}"
  }

  // Draw glowing pill badge
  val pillW = 160f
  val pillH = 32f
  val left = proj.first - pillW / 2f
  val top = proj.second - 40f

  scope.drawRoundRect(
    color = MaranaoGold,
    topLeft = Offset(left - 2f, top - 2f),
    size = Size(pillW + 4f, pillH + 4f),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
  )
  scope.drawRoundRect(
    color = SurfaceDark,
    topLeft = Offset(left, top),
    size = Size(pillW, pillH),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
  )
}
