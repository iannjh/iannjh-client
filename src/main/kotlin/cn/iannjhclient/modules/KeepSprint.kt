package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import org.lwjgl.glfw.GLFW

object KeepSprint : Module("KeepSprint", "Allows sprinting while attacking", Category.MOVEMENT) {
    init {
        this.key = GLFW.GLFW_KEY_UNKNOWN
        enabled = true
    }
}
