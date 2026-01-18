package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.gui.YamlEditor
import org.lwjgl.glfw.GLFW

object YamlEditorModule : Module("YamlEditor", "Open Yaml Editor", Category.CLIENT) {
    init {
        // 绑定到右 Shift (GLFW.GLFW_KEY_RIGHT_SHIFT)
        this.key = GLFW.GLFW_KEY_RIGHT_SHIFT
    }

    override fun onEnable() {
        this.enabled = true // 确保设置enabled状态
        if (mc.currentScreen !is cn.iannjhclient.gui.YamlEditor) {
            mc.setScreen(YamlEditor())
        }
    }


    override fun onDisable() {
        // 不做任何操作，因为YamlEditor自己会处理关闭
    }
}