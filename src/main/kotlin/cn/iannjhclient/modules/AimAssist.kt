package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.value.BooleanSetting
import cn.iannjhclient.value.FloatSetting
import cn.iannjhclient.value.ModeSetting
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object AimAssist : Module("AimAssist", "Assists with aiming at entities", Category.COMBAT) {
    // Settings
    private val speed = FloatSetting("Speed", "How fast to aim at target", 3.0f, 10.0f, 0.1f, 0.1f)
    private val fov = FloatSetting("FOV", "Maximum FOV to target", 90.0f, 360.0f, 10.0f, 5.0f)
    private val distance = FloatSetting("Distance", "Maximum distance to target", 6.0f, 20.0f, 1.0f, 0.5f)
    private val onlyPlayers = BooleanSetting("OnlyPlayers", "Only target players", false)
    private val clickAim = BooleanSetting("ClickAim", "Only aim when clicking", true)
    private val aimMode = ModeSetting("AimMode", "Aim mode", "Smooth", mutableListOf("Smooth", "Silent"))
    private val verticalAim = BooleanSetting("VerticalAim", "Enable vertical aiming", true)
    private val ignoreTeammates = BooleanSetting("IgnoreTeammates", "Ignore teammates", true)

    init {
        this.key = GLFW.GLFW_KEY_R
    }

    private var target: LivingEntity? = null

    override fun onTick() {
        if (mc.player == null || mc.world == null) return

        // Check if should aim (if ClickAim is enabled, only aim when clicking)
        if (clickAim.value && !mc.options.attackKey.isPressed) {
            target = null
            return
        }

        // Find target
        target = findTarget()

        if (target != null) {
            aimAtTarget(target!!)
        }
    }

    private fun findTarget(): LivingEntity? {
        val player = mc.player ?: return null
        val world = mc.world ?: return null

        var closestEntity: LivingEntity? = null
        var closestDistance = Float.MAX_VALUE

        for (entity in world.entities) {
            // Skip if entity is not a LivingEntity
            if (entity !is LivingEntity) continue

            // Skip if entity is the player
            if (entity === player) continue

            // Skip if entity is dead
            if (entity.isDead) continue

            // Skip if only players is enabled and entity is not a player
            if (onlyPlayers.value && entity !is PlayerEntity) continue

            // Skip if ignore teammates is enabled and entity is on the same team
            if (ignoreTeammates.value && entity.isTeammate(player)) continue

            // Calculate distance to entity
            val entityDistance = player.distanceTo(entity)

            // Skip if entity is too far
            if (entityDistance > distance.value) continue

            // Check if entity is in FOV
            val yawDiff = MathHelper.wrapDegrees(entity.yaw - player.yaw)
            val pitchDiff = MathHelper.wrapDegrees(entity.pitch - player.pitch)
            val fovDiff = sqrt((yawDiff * yawDiff + pitchDiff * pitchDiff).toDouble()).toFloat()

            if (fovDiff > fov.value / 2) continue

            // Check if this is the closest entity
            if (entityDistance < closestDistance) {
                closestDistance = entityDistance
                closestEntity = entity
            }
        }

        return closestEntity
    }

    private fun aimAtTarget(target: LivingEntity) {
        val player = mc.player ?: return

        // Calculate needed rotations
        val neededYaw = calculateYaw(target)
        val neededPitch = calculatePitch(target)

        // Smoothly rotate towards target
        val yawSpeed = speed.value
        val pitchSpeed = speed.value

        if (aimMode.value == "Smooth") {
            // Smooth aim mode - gradually rotate towards target
            player.yaw = smoothRotate(player.yaw, neededYaw, yawSpeed)
            if (verticalAim.value) {
                player.pitch = smoothRotate(player.pitch, neededPitch, pitchSpeed)
            }
        } else {
            // Silent aim mode - modify rotation without visible movement
            // Note: This is a simplified version and may need additional implementation
            player.yaw = neededYaw
            if (verticalAim.value) {
                player.pitch = neededPitch
            }
        }
    }

    private fun calculateYaw(entity: Entity): Float {
        val player = mc.player ?: return 0f
        val x = entity.x - player.x
        val z = entity.z - player.z
        return MathHelper.wrapDegrees((atan2(x, z) * 180.0 / Math.PI).toFloat() * -1.0f)
    }

    private fun calculatePitch(entity: Entity): Float {
        val player = mc.player ?: return 0f
        val x = entity.x - player.x
        val y = (entity.eyeY - player.eyeY)
        val z = entity.z - player.z
        val dist = sqrt((x * x + z * z).toDouble())

        return MathHelper.wrapDegrees((-atan2(y, dist) * 180.0 / Math.PI).toFloat())
    }

    private fun smoothRotate(current: Float, target: Float, speed: Float): Float {
        var diff = MathHelper.wrapDegrees(target - current)

        // Limit the rotation speed
        diff = diff.coerceIn(-speed, speed)

        return current + diff
    }
}
