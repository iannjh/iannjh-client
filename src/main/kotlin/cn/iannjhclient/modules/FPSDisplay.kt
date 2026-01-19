package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.event.events.DrawEvent
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

object FPSDisplay : Module("FPSDisplay", "Displays current FPS", Category.RENDER) {
    init {
        key = GLFW.GLFW_KEY_UNKNOWN
        enabled = true
    }

    override fun onDraw(event: DrawEvent) {
        if (!enabled) return

        val mc = MinecraftClient.getInstance()
        val fps = mc.currentFps
        val color = when {
            fps > 100 -> 0xFF00FF00.toInt() // Green
            fps > 60 -> 0xFFFFFF00.toInt() // Yellow
            else -> 0xFFFF0000.toInt() // Red
        }

        event.context.drawTextWithShadow(
            mc.textRenderer,
            "$fps FPS",
            460, 5,
            color
        )
    }
}