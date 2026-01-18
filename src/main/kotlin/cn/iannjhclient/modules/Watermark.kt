package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.event.events.DrawEvent
import cn.iannjhclient.iannjhclient
import org.lwjgl.glfw.GLFW

object Watermark : Module("Watermark", "...", Category.CLIENT) {
    init {
        this.key = GLFW.GLFW_KEY_J
    }

    override fun onDraw(event: DrawEvent) {
        event.context.drawText(mc.textRenderer,"${iannjhclient::class.java.simpleName}v0.1",100,100,-1, true)
    }
}