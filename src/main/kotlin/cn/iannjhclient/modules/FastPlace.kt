package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.event.events.TickEvent
import cn.iannjhclient.value.IntSetting
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW

object FastPlace : Module("FastPlace", "Allows you to place blocks faster", Category.PLAYER) {
    val delaySetting = IntSetting("Delay", "放置延迟", 0, 0, 4, 1)

    init {
        this.key = GLFW.GLFW_KEY_UNKNOWN
        enabled = true
    }
}


