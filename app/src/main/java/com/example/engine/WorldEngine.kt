package com.example.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.model.NPC
import com.example.model.NPCDatabase
import kotlin.math.*

data class Vector3(val x: Float, val y: Float, val z: Float) {
  operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
  operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
  operator fun times(scalar: Float) = Vector3(x * scalar, y * scalar, z * scalar)
  fun length(): Float = sqrt(x * x + y * y + z * z)
}

enum class WorldObjectType {
  TOROGAN_PALACE,
  VILLAGE_HOUSE,
  WEAVING_PAVILION,
  ARTISAN_FORGE,
  LAKE_DOCK,
  AWANG_BOAT,
  MARKET_STALL,
  MOSQUE_PORTAL,
  PALM_TREE,
  STREET_LANTERN
}

data class WorldBuilding(
  val id: String,
  val name: String,
  val type: WorldObjectType,
  val position: Vector3,
  val width: Float,
  val height: Float,
  val depth: Float,
  val rotationY: Float = 0f,
  val primaryColor: Long,
  val accentColor: Long,
  val culturalDetail: String
)

data class ProjectedRenderItem(
  val depth: Float,
  val renderBlock: (drawScope: androidx.compose.ui.graphics.drawscope.DrawScope, width: Float, height: Float) -> Unit
)

class Camera3D {
  var targetPosition: Vector3 = Vector3(0f, 0f, 0f)
  var distance: Float = 8.5f
  var yawDegrees: Float = 180f // Looking towards Torogan by default
  var pitchDegrees: Float = 28f // Comfortable third-person downward angle

  fun getEyePosition(): Vector3 {
    val yawRad = Math.toRadians(yawDegrees.toDouble()).toFloat()
    val pitchRad = Math.toRadians(pitchDegrees.toDouble()).toFloat()
    val camX = targetPosition.x - distance * sin(yawRad) * cos(pitchRad)
    val camY = targetPosition.y + distance * sin(pitchRad)
    val camZ = targetPosition.z - distance * cos(yawRad) * cos(pitchRad)
    return Vector3(camX, max(0.5f, camY), camZ)
  }

  fun projectToScreen(
    point: Vector3,
    screenWidth: Float,
    screenHeight: Float,
    fovScale: Float = screenHeight * 0.95f
  ): Triple<Float, Float, Float>? {
    val eye = getEyePosition()
    val target = targetPosition

    // Forward vector
    var fwd = target - eye
    val fwdLen = fwd.length()
    if (fwdLen < 0.001f) return null
    fwd = fwd * (1f / fwdLen)

    // Up reference
    val upRef = Vector3(0f, 1f, 0f)

    // Right vector = upRef x fwd (normalized)
    var right = Vector3(
      upRef.y * fwd.z - upRef.z * fwd.y,
      upRef.z * fwd.x - upRef.x * fwd.z,
      upRef.x * fwd.y - upRef.y * fwd.x
    )
    val rightLen = right.length()
    if (rightLen < 0.001f) return null
    right = right * (1f / rightLen)

    // True Camera Up vector = fwd x right
    val camUp = Vector3(
      fwd.y * right.z - fwd.z * right.y,
      fwd.z * right.x - fwd.x * right.z,
      fwd.x * right.y - fwd.y * right.x
    )

    // Vector from camera eye to world point
    val toPoint = point - eye

    // Camera space coordinates
    val camX = toPoint.x * right.x + toPoint.y * right.y + toPoint.z * right.z
    val camY = toPoint.x * camUp.x + toPoint.y * camUp.y + toPoint.z * camUp.z
    val camZ = toPoint.x * fwd.x + toPoint.y * fwd.y + toPoint.z * fwd.z

    if (camZ <= 0.3f) return null // Clipped behind or too close to camera plane

    val screenX = (camX / camZ) * fovScale + (screenWidth / 2f)
    val screenY = (screenHeight / 2f) - (camY / camZ) * fovScale

    return Triple(screenX, screenY, camZ)
  }
}

class WorldEngine {
  val camera: Camera3D = Camera3D()
  var playerPosition: Vector3 = Vector3(0f, 0f, 0f)
  var playerFacingDegrees: Float = 0f
  var isRunning: Boolean = false
  var isMoving: Boolean = false
  var walkCycleTimer: Float = 0f
  var waterWaveTimer: Float = 0f
  var timeOfDayHours: Float = 16.5f // Default to glowing golden hour sunset

  val buildings: List<WorldBuilding> = listOf(
    // Ancestral Torogan Palace (Central North)
    WorldBuilding(
      id = "bld_torogan",
      name = "Kialdan a Torogan (Ancestral Palace)",
      type = WorldObjectType.TOROGAN_PALACE,
      position = Vector3(0f, 0f, 18f),
      width = 7.5f,
      height = 5.2f,
      depth = 6.0f,
      primaryColor = 0xFF4A3429, // Torogan dark timber
      accentColor = 0xFFE5A93C, // Golden Okir Panolong beams
      culturalDetail = "The regal ancestral Torogan of the ruling family, featuring soaring Panolong wing beams."
    ),

    // Weaving Pavilion (West)
    WorldBuilding(
      id = "bld_weaving",
      name = "Lakeside Weaving Pavilion",
      type = WorldObjectType.WEAVING_PAVILION,
      position = Vector3(-14f, 0f, 4f),
      width = 5.0f,
      height = 3.8f,
      depth = 4.2f,
      primaryColor = 0xFF2E855C, // Green accent
      accentColor = 0xFFB8286A, // Magenta Langkit cloths
      culturalDetail = "Workshop where master craftswomen weave the Malong Landap on backstrap looms."
    ),

    // Master Forge & Boatyard (East)
    WorldBuilding(
      id = "bld_forge",
      name = "Master Brass Forge & Boatyard",
      type = WorldObjectType.ARTISAN_FORGE,
      position = Vector3(14f, 0f, -2f),
      width = 5.2f,
      height = 3.6f,
      depth = 4.0f,
      primaryColor = 0xFF785644,
      accentColor = 0xFFE5A93C,
      culturalDetail = "The forge of Panday Bilal, resonant with the hammering of Kulintang brass gongs."
    ),

    // Lake Docks (South waterfront)
    WorldBuilding(
      id = "bld_dock",
      name = "Lake Lanao Shore Docks",
      type = WorldObjectType.LAKE_DOCK,
      position = Vector3(0f, -0.1f, -14f),
      width = 6.0f,
      height = 0.5f,
      depth = 10.0f,
      primaryColor = 0xFF5C4033,
      accentColor = 0xFF8B6B4F,
      culturalDetail = "Weathered hardwood pier where fishermen moor traditional Awang dugout boats."
    ),

    // Awang Outrigger Boats moored at dock
    WorldBuilding(
      id = "bld_awang_1",
      name = "Ceremonial Awang Boat",
      type = WorldObjectType.AWANG_BOAT,
      position = Vector3(4.5f, -0.2f, -16f),
      width = 2.0f,
      height = 1.2f,
      depth = 5.5f,
      primaryColor = 0xFF654321,
      accentColor = 0xFFF7CF68,
      culturalDetail = "Traditional Maranao canoe carved from solid trunk with bamboo outrigger floats."
    ),

    WorldBuilding(
      id = "bld_awang_2",
      name = "Fisherman's Awang",
      type = WorldObjectType.AWANG_BOAT,
      position = Vector3(-4.5f, -0.2f, -18f),
      width = 1.8f,
      height = 1.1f,
      depth = 5.0f,
      primaryColor = 0xFF4A3429,
      accentColor = 0xFF2E855C,
      culturalDetail = "Fast lake canoe equipped with nets and bamboo fish traps."
    ),

    // Village Stilt Houses
    WorldBuilding(
      id = "bld_house_west",
      name = "Elder's Stilt Home",
      type = WorldObjectType.VILLAGE_HOUSE,
      position = Vector3(-18f, 0f, 16f),
      width = 4.2f,
      height = 3.5f,
      depth = 4.0f,
      primaryColor = 0xFF5A4232,
      accentColor = 0xFFDFD1B8,
      culturalDetail = "Elevated wooden home resting on sturdy tree trunks, keeping it cool above the soil."
    ),

    WorldBuilding(
      id = "bld_house_east",
      name = "Fisherman's Family House",
      type = WorldObjectType.VILLAGE_HOUSE,
      position = Vector3(18f, 0f, 14f),
      width = 4.2f,
      height = 3.5f,
      depth = 4.0f,
      primaryColor = 0xFF5A4232,
      accentColor = 0xFFDFD1B8,
      culturalDetail = "Traditional family dwelling with bamboo lattice windows overlooking the lake."
    ),

    // Marketplace Stalls
    WorldBuilding(
      id = "bld_market_1",
      name = "Textile & Pearl Bazaar",
      type = WorldObjectType.MARKET_STALL,
      position = Vector3(-6f, 0f, -2f),
      width = 3.0f,
      height = 2.4f,
      depth = 2.5f,
      primaryColor = 0xFF801A24,
      accentColor = 0xFFE5A93C,
      culturalDetail = "Market stalls displaying vibrantly colored woven cloths and freshwater pearls."
    ),

    WorldBuilding(
      id = "bld_market_2",
      name = "Sweet Dodol & Lake Provisions",
      type = WorldObjectType.MARKET_STALL,
      position = Vector3(6f, 0f, 2f),
      width = 3.0f,
      height = 2.4f,
      depth = 2.5f,
      primaryColor = 0xFF1F593E,
      accentColor = 0xFFF7CF68,
      culturalDetail = "Traders offering fresh Dodol, turmeric rice, and fragrant Mindanao spices."
    ),

    // Peaceful Mosque Courtyard Portal (Far North sanctuary)
    WorldBuilding(
      id = "bld_mosque_gate",
      name = "Mosque Courtyard & Sacred Gateway",
      type = WorldObjectType.MOSQUE_PORTAL,
      position = Vector3(0f, 0f, 32f),
      width = 8.0f,
      height = 5.5f,
      depth = 5.0f,
      primaryColor = 0xFFFDF8EE,
      accentColor = 0xFF2E855C,
      culturalDetail = "The peaceful community prayer courtyard, treated with reverence by all villagers."
    )
  )

  // Decorative trees
  val palmTreePositions = listOf(
    Vector3(-10f, 0f, 10f),
    Vector3(-8f, 0f, 22f),
    Vector3(10f, 0f, 8f),
    Vector3(12f, 0f, 22f),
    Vector3(-22f, 0f, -4f),
    Vector3(22f, 0f, -6f),
    Vector3(-6f, 0f, -10f),
    Vector3(6f, 0f, -10f)
  )

  fun updateMovement(inputX: Float, inputY: Float, deltaTimeSec: Float) {
    val inputMag = sqrt(inputX * inputX + inputY * inputY)
    if (inputMag > 0.05f) {
      isMoving = true
      val baseSpeed = if (isRunning) 5.5f else 3.2f
      val speed = baseSpeed * min(1.0f, inputMag)

      // Align movement with current camera yaw
      val inputAngle = atan2(inputX.toDouble(), -inputY.toDouble()).toFloat()
      val camYawRad = Math.toRadians(camera.yawDegrees.toDouble()).toFloat()
      val moveDirRad = inputAngle + camYawRad

      val moveX = sin(moveDirRad) * speed * deltaTimeSec
      val moveZ = cos(moveDirRad) * speed * deltaTimeSec

      val nextX = playerPosition.x + moveX
      val nextZ = playerPosition.z + moveZ

      // Collision boundary check with lake edge and map borders
      if (canMoveTo(nextX, nextZ)) {
        playerPosition = Vector3(nextX, 0f, nextZ)
      } else if (canMoveTo(nextX, playerPosition.z)) {
        playerPosition = Vector3(nextX, 0f, playerPosition.z)
      } else if (canMoveTo(playerPosition.x, nextZ)) {
        playerPosition = Vector3(playerPosition.x, 0f, nextZ)
      }

      // Player faces movement direction
      playerFacingDegrees = Math.toDegrees(moveDirRad.toDouble()).toFloat()

      // Walk cycle animation timer
      walkCycleTimer += deltaTimeSec * (if (isRunning) 12f else 7f)
    } else {
      isMoving = false
      walkCycleTimer = 0f
    }

    // Water wave oscillation
    waterWaveTimer += deltaTimeSec * 2.5f

    // Camera smoothly follows player target
    camera.targetPosition = playerPosition
  }

  fun canMoveTo(targetX: Float, targetZ: Float): Boolean {
    // Map outer bounds
    if (abs(targetX) > 28f || targetZ > 36f || targetZ < -26f) return false

    // Lake water boundary: z < -10 is water UNLESS player is on the wooden dock:
    // Dock is between x: [-3.5, 3.5] and z: [-9, -24]
    if (targetZ < -10f) {
      val onDock = abs(targetX) <= 3.6f && targetZ >= -24.5f
      if (!onDock) return false // Prevent swimming into deep lake without boat
    }

    // Building collision bounding boxes
    for (b in buildings) {
      val halfW = b.width / 2f + 0.6f
      val halfD = b.depth / 2f + 0.6f
      if (abs(targetX - b.position.x) < halfW && abs(targetZ - b.position.z) < halfD) {
        // Collided with building solid footprint
        return false
      }
    }

    return true
  }

  fun findNearestInteractable(range: Float = 2.8f): InteractableTarget? {
    // 1. Check nearby NPCs
    for (npc in NPCDatabase.villagers) {
      val dx = playerPosition.x - npc.worldX
      val dz = playerPosition.z - npc.worldZ
      val dist = sqrt(dx * dx + dz * dz)
      if (dist <= range) {
        return InteractableTarget.NpcTarget(npc, dist)
      }
    }

    // 2. Check nearby buildings / landmarks
    for (b in buildings) {
      val dx = playerPosition.x - b.position.x
      val dz = playerPosition.z - b.position.z
      val dist = sqrt(dx * dx + dz * dz)
      val effectiveRadius = (b.width + b.depth) / 4f + 1.8f
      if (dist <= effectiveRadius) {
        return InteractableTarget.BuildingTarget(b, dist)
      }
    }

    return null
  }
}

sealed class InteractableTarget {
  data class NpcTarget(val npc: NPC, val distance: Float) : InteractableTarget()
  data class BuildingTarget(val building: WorldBuilding, val distance: Float) : InteractableTarget()
}
