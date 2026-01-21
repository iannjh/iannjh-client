
package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.event.events.DrawEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.LivingEntity
import org.lwjgl.opengl.GL11
import java.awt.Color

object Minimap : Module("Minimap", "Displays a square minimap with entities", Category.RENDER) {
    private const val MAP_SIZE = 100  // 地图大小（像素）
    private const val MAP_RANGE = 50 // 地图显示范围（方块）
    private const val ENTITY_DOT_SIZE = 3 // 实体点大小

    init {
        key = org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN
        enabled = true
    }

    override fun onDraw(event: DrawEvent) {
        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: return
        val window = mc.window

        // 地图位置（屏幕右下角）
        val mapX = window.scaledWidth - MAP_SIZE - 10
        val mapY = window.scaledHeight - MAP_SIZE - 10

        // 保存当前矩阵状态
        event.context.matrices.push()

        // 启用混合模式
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        // 绘制地图背景
        drawMapBackground(event.context, mapX, mapY, player)

        // 绘制实体点
        drawEntities(event.context, mapX, mapY, player)

        // 绘制十字线
        drawCrosshair(event.context, mapX, mapY)

        // 绘制地图边框
        drawMapBorder(event.context, mapX, mapY)

        // 禁用混合模式
        GL11.glDisable(GL11.GL_BLEND)

        // 恢复矩阵状态
        event.context.matrices.pop()
    }

    private fun drawMapBackground(context: DrawContext, x: Int, y: Int, player: net.minecraft.entity.player.PlayerEntity) {
        // 绘制半透明黑色背景
        // 使用DrawContext的fill方法绘制背景
        context.fill(x, y, x + MAP_SIZE, y + MAP_SIZE, Color(0, 0, 0, 150).rgb)

        // 绘制网格线（可选，增加地图可读性）
        val gridSize = 10 // 网格大小（像素）
        val gridColor = Color(255, 255, 255, 50).rgb

        // 垂直网格线
        for (i in 0..MAP_SIZE step gridSize) {
            context.fill(x + i, y, x + i + 1, y + MAP_SIZE, gridColor)
        }

        // 水平网格线
        for (i in 0..MAP_SIZE step gridSize) {
            context.fill(x, y + i, x + MAP_SIZE, y + i + 1, gridColor)
        }
    }

    private fun drawEntities(context: DrawContext, mapX: Int, mapY: Int, player: net.minecraft.entity.player.PlayerEntity) {
        val mc = MinecraftClient.getInstance()
        val world = mc.world ?: return

        // 遍历世界中的实体
        for (entity in world.entities) {
            // 只绘制附近的实体
            if (entity.distanceTo(player) > MAP_RANGE) continue

            // 计算实体在地图上的位置
            val dx = entity.x - player.x
            val dz = entity.z - player.z

            // 将方块坐标转换为像素坐标
            // 注意：这里使用固定的方位，不随玩家旋转
            val pixelX = mapX + MAP_SIZE / 2 + (dx * MAP_SIZE / MAP_RANGE / 2).toInt()
            val pixelY = mapY + MAP_SIZE / 2 + (dz * MAP_SIZE / MAP_RANGE / 2).toInt()

            // 确保点在地图范围内
            if (pixelX < mapX || pixelX > mapX + MAP_SIZE || 
                pixelY < mapY || pixelY > mapY + MAP_SIZE) continue

            // 绘制实体点
            val dotColor = if (entity is LivingEntity) Color.RED else Color.ORANGE
            context.fill(
                pixelX - ENTITY_DOT_SIZE / 2,
                pixelY - ENTITY_DOT_SIZE / 2,
                pixelX + ENTITY_DOT_SIZE / 2,
                pixelY + ENTITY_DOT_SIZE / 2,
                dotColor.rgb
            )
        }
    }

    private fun drawCrosshair(context: DrawContext, mapX: Int, mapY: Int) {
        val centerX = mapX + MAP_SIZE / 2
        val centerY = mapY + MAP_SIZE / 2
        val crosshairColor = Color.WHITE.rgb

        // 水平线
        context.fill(mapX, centerY, mapX + MAP_SIZE, centerY + 1, crosshairColor)

        // 垂直线
        context.fill(centerX, mapY, centerX + 1, mapY + MAP_SIZE, crosshairColor)
    }

    private fun drawMapBorder(context: DrawContext, mapX: Int, mapY: Int) {
        val borderColor = Color.WHITE.rgb

        // 上边框
        context.fill(mapX, mapY, mapX + MAP_SIZE, mapY + 1, borderColor)

        // 下边框
        context.fill(mapX, mapY + MAP_SIZE - 1, mapX + MAP_SIZE, mapY + MAP_SIZE, borderColor)

        // 左边框
        context.fill(mapX, mapY, mapX + 1, mapY + MAP_SIZE, borderColor)

        // 右边框
        context.fill(mapX + MAP_SIZE - 1, mapY, mapX + MAP_SIZE, mapY + MAP_SIZE, borderColor)
    }
}
