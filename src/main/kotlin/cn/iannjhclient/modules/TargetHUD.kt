package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.event.events.DrawEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.cos

object TargetHUD : Module("TargetHUD", "Displays targeted entity info", Category.RENDER) {
    private val clicks = mutableMapOf<Int, Queue<Long>>()
    private var wasPressed = false
    private var lastPressed = 0L
    private var savedTarget: LivingEntity? = null

    init {
        key = GLFW.GLFW_KEY_UNKNOWN
        enabled = true
        // Initialize CPS tracking for mouse buttons
        clicks[GLFW.GLFW_MOUSE_BUTTON_1] = LinkedList() // Left click
    }

    override fun onDraw(event: DrawEvent) {
        if (!enabled) return

        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: return
        val target = mc.targetedEntity

        // Track clicks
        trackClicks(mc)

        // Check if target is valid and not an ItemFrame
        if (target == null || target is ItemFrameEntity) {
            savedTarget = null
            return
        }

        if (target is LivingEntity) {
            savedTarget = target
            renderTargetHUD(event, target, player)
        }
    }

    private fun trackClicks(mc: MinecraftClient) {
        val pressed = GLFW.glfwGetMouseButton(mc.window.handle, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
        if (pressed != wasPressed) {
            lastPressed = System.currentTimeMillis()
            wasPressed = pressed
            if (pressed) {
                clicks[GLFW.GLFW_MOUSE_BUTTON_1]?.add(lastPressed)
            }
        }

        // Clean up old clicks
        val now = System.currentTimeMillis()
        clicks[GLFW.GLFW_MOUSE_BUTTON_1]?.removeIf { now - it > 1000 }
    }

    private fun getCPS(): Int {
        return clicks[GLFW.GLFW_MOUSE_BUTTON_1]?.size ?: 0
    }

    private fun renderTargetHUD(event: DrawEvent, target: LivingEntity, player: LivingEntity) {
        val mc = MinecraftClient.getInstance()
        val context = event.context
        val matrices = context.matrices
        val textRenderer = mc.textRenderer

        // Position settings
        val x = mc.window.scaledWidth / 2 + 10
        val y = mc.window.scaledHeight / 2 - 20

        // Draw rounded rectangle background
        val bgColor = Color(0, 0, 0, 170)
        val width = 123
        val height = 58
        drawRoundedRect(context, x.toFloat(), y.toFloat(), (x + width).toFloat(), (y + height).toFloat(), 5f, bgColor)

        // Draw player head
        if (target is net.minecraft.entity.player.PlayerEntity) {
            drawPlayerHead(context, x + 10, y + 20, 16, target)
        }

        // Draw name
        context.drawTextWithShadow(
            textRenderer,
            target.name.string,
            x + 28,
            y + 16,
            0xFFFFFF
        )

        // Draw health bar
        renderHealthBar(context, x + 27, y + 27, target)

        // Draw distance
        val distance = player.distanceTo(target)
        context.drawTextWithShadow(
            textRenderer,
            String.format("§fDistance: §r%.2f", distance),
            x + 5,
            y + 38,
            Color(233, 55, 65).rgb
        )
    }

    private fun renderHealthBar(context: DrawContext, x: Int, y: Int, target: LivingEntity) {
        val health = target.health
        val maxHealth = target.maxHealth
        val healthPercentage = (health / maxHealth).coerceIn(0f, 1f)
        val barWidth = 93
        val barHeight = 8

        // Draw background
        context.fill(x, y, x + barWidth, y + barHeight, Color(25, 23, 13).rgb)

        // Draw health bar with color based on health percentage
        val healthColor = when {
            healthPercentage > 0.6 -> Color(0, 255, 0)
            healthPercentage > 0.3 -> Color(255, 255, 0)
            else -> Color(233, 55, 65)
        }
        context.fill(
            x, y,
            x + (barWidth * healthPercentage).toInt(),
            y + barHeight,
            healthColor.rgb
        )
    }

    private fun drawPlayerHead(context: DrawContext, x: Int, y: Int, size: Int, player: net.minecraft.entity.player.PlayerEntity) {
        val mc = MinecraftClient.getInstance()
        val playerInfo: PlayerListEntry? = mc.networkHandler?.getPlayerListEntry(player.uuid)

        if (playerInfo != null) {
            val skin = playerInfo.skinTextures.texture()
            context.drawTexture(
                skin,
                x - 5, y - 5,
                8.0f, 8.0f,
                20, 20,
                64, 64
            )
        }
    }

    private fun drawRoundedRect(context: DrawContext, x: Float, y: Float, x1: Float, y1: Float, radius: Float, color: Color) {
        val matrices = context.matrices
        matrices.push()

        // Draw main rectangle
        context.fill(
            x.toInt() + radius.toInt(),
            y.toInt(),
            x1.toInt() - radius.toInt(),
            y1.toInt(),
            color.rgb
        )

        // Draw left and right rectangles
        context.fill(
            x.toInt(),
            y.toInt() + radius.toInt(),
            x1.toInt(),
            y1.toInt() - radius.toInt(),
            color.rgb
        )

        // Draw rounded corners using small rectangles
        val cornerSize = radius.toInt()
        for (i in 0 until cornerSize) {
            for (j in 0 until cornerSize) {
                val dx = i - cornerSize
                val dy = j - cornerSize
                if (dx * dx + dy * dy <= cornerSize * cornerSize) {
                    // Top-left corner
                    context.fill(
                        x.toInt() + i,
                        y.toInt() + j,
                        x.toInt() + i + 1,
                        y.toInt() + j + 1,
                        color.rgb
                    )
                    // Top-right corner
                    context.fill(
                        x1.toInt() - cornerSize + i,
                        y.toInt() + j,
                        x1.toInt() - cornerSize + i + 1,
                        y.toInt() + j + 1,
                        color.rgb
                    )
                    // Bottom-left corner
                    context.fill(
                        x.toInt() + i,
                        y1.toInt() - cornerSize + j,
                        x.toInt() + i + 1,
                        y1.toInt() - cornerSize + j + 1,
                        color.rgb
                    )
                    // Bottom-right corner
                    context.fill(
                        x1.toInt() - cornerSize + i,
                        y1.toInt() - cornerSize + j,
                        x1.toInt() - cornerSize + i + 1,
                        y1.toInt() - cornerSize + j + 1,
                        color.rgb
                    )
                }
            }
        }

        matrices.pop()
    }
}