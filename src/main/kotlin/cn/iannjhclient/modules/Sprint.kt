package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.value.BooleanSetting
import org.lwjgl.glfw.GLFW

object Sprint : Module("Sprint", "...", Category.MOVEMENT) {
    val test = BooleanSetting("test","sb.",false)
    init {
        this.enabled = true
        this.key = GLFW.GLFW_KEY_I
    }


    override fun onTick() {
        if ((mc.player != null && mc.world != null )) {
            mc.player!!.isSprinting = true
        }

    }

}