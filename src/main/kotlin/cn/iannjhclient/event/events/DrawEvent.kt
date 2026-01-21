// DrawEvent.kt
package cn.iannjhclient.event.events

import cn.iannjhclient.event.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack

class DrawEvent(val context: DrawContext, val tickDelta: Float) : Event() {
    val matrices: MatrixStack
        get() = context.matrices
}