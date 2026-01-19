package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.ModuleManager
import cn.iannjhclient.event.events.DrawEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window
import org.lwjgl.glfw.GLFW

object ArrayList: Module("ArrayList", "Displays enabled modules", Category.RENDER) {
    init {
        key = GLFW.GLFW_KEY_X
        // 确保模块默认启用
        enabled = true
    }

    override fun onDraw(event: DrawEvent) {
        if (!enabled) return

        val mc = MinecraftClient.getInstance()
        val window: Window = mc.window
        val screenWidth = window.scaledWidth
        var y = 20 // 从屏幕顶部开始，留出一些空间
        val rightMargin = 5 // 右侧留出5像素的边距

        // 按标签长度从长到短排序模块
        val sortedModules = ModuleManager.modules
            .filter { it.enabled && it != this } // 过滤出启用的模块，排除自己
            .sortedByDescending { mc.textRenderer.getWidth(it.name) }

        // 绘制模块列表
        sortedModules.forEach { module ->
            val textWidth = mc.textRenderer.getWidth(module.name)
            val textHeight = mc.textRenderer.fontHeight + 2

            // 计算x坐标，使文字靠右对齐
            val x = screenWidth - rightMargin - textWidth

            // 绘制半透明背景（根据文本宽度）
            event.context.fill(
                x - 2, y - 1,
                x + textWidth + 2,
                y + textHeight - 1,
                0x60000000.toInt() // 半透明黑色背景
            )

            // 计算动态渐变颜色（从蓝色到青绿色）
            val time = System.currentTimeMillis() / 1000.0
            val colorOffset = (Math.sin(time) + 1) / 2 // 0到1之间的值

            // 蓝色(0x00, 0x7F, 0xFF)到青绿色(0x00, 0xFF, 0x7F)的渐变
            val red = 0
            val green = (127 + (255 - 127) * colorOffset).toInt()
            val blue = (255 - (255 - 127) * colorOffset).toInt()
            val color = (red shl 16) or (green shl 8) or blue or 0xFF000000.toInt()

            // 绘制带阴影的渐变色文本
            event.context.drawTextWithShadow(
                mc.textRenderer,
                module.name,
                x, y,
                color
            )

            // 在模块标签最右侧绘制白色竖线条
            event.context.fill(
                x + textWidth + 2, y - 1,
                x + textWidth + 3,
                y + textHeight - 1,
                0xFFFFFFFF.toInt() // 白色
            )

            y += textHeight
        }
    }
}