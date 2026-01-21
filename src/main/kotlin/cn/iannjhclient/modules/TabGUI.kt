
package cn.iannjhclient.modules

import cn.iannjhclient.Category
import cn.iannjhclient.Module
import cn.iannjhclient.ModuleManager
import cn.iannjhclient.event.events.DrawEvent
import cn.iannjhclient.event.events.KeyboardEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import java.awt.Color

object TabGUI : Module("TabGUI", "Tab-based GUI for module management", Category.CLIENT) {
    private var selectedCategory = 0
    private var selectedModule = 0
    private var expanded = false
    private var animationProgress = 0f

    // TabGUI配置
    private val logoWidth = 50
    private val logoHeight = 50
    private val headerHeight = 10
    private val itemHeight = 12
    private val itemWidth = 60
    private val spacing = 2

    init {
        this.key = GLFW.GLFW_KEY_UNKNOWN
        this.enabled = true
        this.defaultEnabled = true
    }

    override fun onDraw(event: DrawEvent) {
        if (!enabled) return

        val mc = MinecraftClient.getInstance()
        val matrices = event.context.matrices
        val window = mc.window
        val screenWidth = window.scaledWidth
        val screenHeight = window.scaledHeight

        // 计算位置
        val startX = 5
        val startY = 5

        // 绘制logo
        drawLogo(event.context, startX, startY)

        // 计算标签栏起始位置（logo下方）
        val tabStartY = startY + logoHeight + 5

        // 绘制TabGUI
        drawTabGUI(event, startX, tabStartY)
    }

    private fun drawLogo(context: DrawContext, x: Int, y: Int) {
        val mc = MinecraftClient.getInstance()

        try {
            // 尝试加载并绘制logo图片
            val identifier = net.minecraft.util.Identifier("minecraft", "textures/heckert_gnu.transp.small.png")
            context.drawTexture(identifier, x, y, 0f, 0f, logoWidth, logoHeight, logoWidth, logoHeight)
        } catch (e: Exception) {
            // 如果图片加载失败，绘制一个占位符
            context.fill(x, y, x + logoWidth, y + logoHeight, Color(0, 100, 200, 150).rgb)
            context.drawText(mc.textRenderer, "Logo", x + 10, y + 20, Color.WHITE.rgb, true)
        }
    }

    private fun drawTabGUI(event: DrawEvent, x: Int, y: Int) {
        val mc = MinecraftClient.getInstance()
        val context = event.context
        val matrices = event.context.matrices

        // 获取所有分类
        val categories = Category.values()

        // 绘制标题栏（iannjhclient）
        val titleHeight = headerHeight / 2
        context.fill(x, y, x + itemWidth, y + titleHeight, Color(0, 0, 0, 180).rgb)
        context.drawText(mc.textRenderer, "iannjhclient", x + 5, y + 1, Color.WHITE.rgb, false)

        // 绘制分类标签
        var currentY = y + titleHeight
        for (i in categories.indices) {
            val category = categories[i]
            val isSelected = i == selectedCategory

            // 绘制标签背景
            context.fill(
                x, currentY,
                x + itemWidth, currentY + itemHeight,
                if (isSelected) Color(0, 100, 200, 180).rgb else Color(0, 0, 0, 150).rgb
            )

            // 绘制标签边框
            context.drawBorder(x, currentY, itemWidth, itemHeight, Color.WHITE.rgb)

            // 绘制标签文本
            context.drawText(
                mc.textRenderer,
                category.name,
                x + 5, currentY + 2,
                Color.WHITE.rgb, false
            )

            currentY += itemHeight + spacing
        }

        // 如果展开，绘制模块列表
        if (expanded) {
            drawModulesList(event, x + itemWidth + spacing, y + titleHeight + selectedCategory * (itemHeight + spacing))
        }
    }

    private fun drawModulesList(event: DrawEvent, x: Int, y: Int) {
        val mc = MinecraftClient.getInstance()
        val context = event.context

        // 获取当前分类的模块
        val category = Category.values()[selectedCategory]
        val modules = ModuleManager.modules.filter { it.category == category }

        // 绘制模块列表
        var currentY = y
        for (i in modules.indices) {
            val module = modules[i]
            val isSelected = i == selectedModule

            // 绘制模块背景
            context.fill(
                x, currentY,
                x + itemWidth, currentY + itemHeight,
                if (isSelected) Color(0, 100, 200, 180).rgb else Color(0, 0, 0, 150).rgb
            )

            // 绘制模块边框
            context.drawBorder(x, currentY, itemWidth, itemHeight, Color.WHITE.rgb)

            // 绘制模块文本
            context.drawText(
                mc.textRenderer,
                module.name,
                x + 5, currentY + 2,
                if (module.enabled) Color(0, 255, 0).rgb else Color.WHITE.rgb, false
            )

            currentY += itemHeight + spacing
        }
    }

    private fun DrawContext.drawBorder(x: Int, y: Int, width: Int, height: Int, color: Int) {
        // 绘制白色边框
        this.fill(x, y, x + width, y + 1, color) // 上边
        this.fill(x, y + height - 1, x + width, y + height, color) // 下边
        this.fill(x, y, x + 1, y + height, color) // 左边
        this.fill(x + width - 1, y, x + width, y + height, color) // 右边
    }

    fun onKeyPress(key: Int, scancode: Int, modifiers: Int): Boolean {
        if (!enabled) return false

        val categories = Category.values()

        when (key) {
            GLFW.GLFW_KEY_UP -> {
                if (expanded) {
                    // 在模块列表中向上移动
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty()) {
                        selectedModule = (selectedModule - 1 + modules.size) % modules.size
                    }
                } else {
                    // 在分类列表中向上移动
                    selectedCategory = (selectedCategory - 1 + categories.size) % categories.size
                }
                return true
            }
            GLFW.GLFW_KEY_DOWN -> {
                if (expanded) {
                    // 在模块列表中向下移动
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty()) {
                        selectedModule = (selectedModule + 1) % modules.size
                    }
                } else {
                    // 在分类列表中向下移动
                    selectedCategory = (selectedCategory + 1) % categories.size
                }
                return true
            }
            GLFW.GLFW_KEY_RIGHT -> {
                // 展开模块列表
                if (!expanded) {
                    expanded = true
                    selectedModule = 0
                }
                return true
            }
            GLFW.GLFW_KEY_LEFT -> {
                // 收起模块列表
                if (expanded) {
                    expanded = false
                }
                return true
            }
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                // 切换选中的模块
                if (expanded) {
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty() && selectedModule < modules.size) {
                        modules[selectedModule].toggle()
                    }
                }
                return true
            }
        }
        return false
    }

    fun onKey(event: KeyboardEvent) {
        if (!enabled) return

        val categories = Category.values()

        when (event.key) {
            GLFW.GLFW_KEY_UP -> {
                if (expanded) {
                    // 在模块列表中向上移动
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty()) {
                        selectedModule = (selectedModule - 1 + modules.size) % modules.size
                    }
                } else {
                    // 在分类列表中向上移动
                    selectedCategory = (selectedCategory - 1 + categories.size) % categories.size
                }
            }
            GLFW.GLFW_KEY_DOWN -> {
                if (expanded) {
                    // 在模块列表中向下移动
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty()) {
                        selectedModule = (selectedModule + 1) % modules.size
                    }
                } else {
                    // 在分类列表中向下移动
                    selectedCategory = (selectedCategory + 1) % categories.size
                }
            }
            GLFW.GLFW_KEY_RIGHT -> {
                // 展开模块列表
                if (!expanded) {
                    expanded = true
                    selectedModule = 0
                }
            }
            GLFW.GLFW_KEY_LEFT -> {
                // 收起模块列表
                if (expanded) {
                    expanded = false
                }
            }
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                // 切换选中的模块
                if (expanded) {
                    val modules = ModuleManager.modules.filter { it.category == categories[selectedCategory] }
                    if (modules.isNotEmpty() && selectedModule < modules.size) {
                        modules[selectedModule].toggle()
                    }
                }
            }
        }
    }
}
