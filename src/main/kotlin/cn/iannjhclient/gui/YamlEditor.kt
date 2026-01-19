package cn.iannjhclient.gui

import cn.iannjhclient.config.ConfigManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class YamlEditor : Screen(Text.of("YamlEditor.kt")) {
    private var yamlContent = ""
    private var originalContent = ""
    private var scrollOffset = 0
    private val maxScrollOffset = 0

    // 光标位置
    private var cursorLine = 0
    private var cursorColumn = 0
    private var cursorVisible = true
    private var cursorBlinkTimer: Float = 0f

    // 按钮区域
    private val buttonHeight = 25
    private val buttonWidth = 80
    private val buttonSpacing = 20

    // 文本编辑区域
    private val editorMargin = 20
    private var editorX = 0
    private var editorY = 0
    private var editorWidth = 0
    private var editorHeight = 0

    // 按钮位置
    private var saveButtonX = 0
    private var saveButtonY = 0
    private var exitButtonX = 0
    private var exitButtonY = 0
    private var resetButtonX = 0
    private var resetButtonY = 0

    init {
        loadYamlContent()
    }

    private fun loadYamlContent() {
        try {
            val configFile = Paths.get("config/iannjhclient/modules.yaml").toFile()
            if (configFile.exists()) {
                yamlContent = String(Files.readAllBytes(configFile.toPath()))
                originalContent = yamlContent
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun init() {
        super.init()
        val client = MinecraftClient.getInstance()
        val window = client.window
        val screenWidth = window.scaledWidth
        val screenHeight = window.scaledHeight

        // 计算编辑区域位置和大小
        editorX = editorMargin
        editorY = editorMargin
        editorWidth = screenWidth - editorMargin * 2
        editorHeight = screenHeight - editorMargin * 2 - buttonHeight - buttonSpacing

        // 计算按钮位置
        val buttonsWidth = buttonWidth * 3 + buttonSpacing * 2
        val buttonsStartX = (screenWidth - buttonsWidth) / 2
        val buttonsY = screenHeight - editorMargin - buttonHeight

        saveButtonX = buttonsStartX
        saveButtonY = buttonsY

        exitButtonX = buttonsStartX + buttonWidth + buttonSpacing
        exitButtonY = buttonsY

        resetButtonX = buttonsStartX + buttonWidth * 2 + buttonSpacing * 2
        resetButtonY = buttonsY
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val client = MinecraftClient.getInstance()
        val window = client.window
        val screenWidth = window.scaledWidth
        val screenHeight = window.scaledHeight

        // 更新光标闪烁
        cursorBlinkTimer += delta
        if (cursorBlinkTimer >= 0.5f) {
            cursorVisible = !cursorVisible
            cursorBlinkTimer = 0f
        }

        // 绘制浅灰色背景覆盖整个屏幕
        context.fill(0, 0, screenWidth, screenHeight, 0xFF383838.toInt())

        // 绘制较亮的灰色编辑框背景
        context.fill(editorX, editorY, editorX + editorWidth, editorY + editorHeight, 0xFF505050.toInt())

        // 绘制编辑框边框
        context.fill(editorX, editorY, editorX + editorWidth, editorY + 2, 0xFFFFFFFF.toInt())
        context.fill(editorX, editorY + editorHeight - 2, editorX + editorWidth, editorY + editorHeight, 0xFFFFFFFF.toInt())
        context.fill(editorX, editorY, editorX + 2, editorY + editorHeight, 0xFFFFFFFF.toInt())
        context.fill(editorX + editorWidth - 2, editorY, editorX + editorWidth, editorY + editorHeight, 0xFFFFFFFF.toInt())

        // 绘制YAML内容
        val textRenderer = client.textRenderer
        val lineHeight = textRenderer.fontHeight + 2
        val lines = yamlContent.split("\n".toRegex())
        val visibleLines = editorHeight / lineHeight

        // 计算可滚动范围
        val totalLines = lines.size
        val maxScroll = maxOf(0, totalLines - visibleLines)
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll
        }

        // 绘制可见行
        for (i in 0 until visibleLines) {
            val lineIndex = scrollOffset + i
            if (lineIndex < lines.size) {
                val line = lines[lineIndex]
                val y = editorY + 5 + i * lineHeight
                context.drawText(textRenderer, line, editorX + 5, y, Color.WHITE.rgb, false)

                // 绘制光标
                if (lineIndex == cursorLine && cursorVisible) {
                    val cursorX = editorX + 5 + textRenderer.getWidth(line.substring(0, cursorColumn.coerceAtMost(line.length)))
                    context.fill(cursorX, y, cursorX + 1, y + textRenderer.fontHeight, 0xFFFFFFFF.toInt())
                }
            }
        }

        // 绘制滚动条
        if (totalLines > visibleLines) {
            val scrollBarHeight = (editorHeight.toFloat() / totalLines * visibleLines).toInt()
            val scrollBarY = editorY + (scrollOffset.toFloat() / maxScroll * (editorHeight - scrollBarHeight)).toInt()
            context.fill(editorX + editorWidth - 10, scrollBarY, editorX + editorWidth - 5, scrollBarY + scrollBarHeight, 0xFFA0A0A0.toInt())
        }

        // 绘制保存按钮（绿色）
        drawButton(context, saveButtonX, saveButtonY, buttonWidth, buttonHeight, "Save", 0xFF00FF00.toInt(), mouseX, mouseY)

        // 绘制退出按钮（红色）
        drawButton(context, exitButtonX, exitButtonY, buttonWidth, buttonHeight, "Exit", 0xFFFF0000.toInt(), mouseX, mouseY)

        // 绘制重置按钮（蓝色）
        drawButton(context, resetButtonX, resetButtonY, buttonWidth, buttonHeight, "Reset", 0xFF0000FF.toInt(), mouseX, mouseY)

        super.render(context, mouseX, mouseY, delta)
    }

    private fun drawButton(context: DrawContext, x: Int, y: Int, width: Int, height: Int, text: String, color: Int, mouseX: Int, mouseY: Int) {
        // 检查鼠标是否悬停在按钮上
        val isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

        // 绘制按钮背景
        context.fill(x, y, x + width, y + height, if (isHovered) (color and 0x00FFFFFF) or 0xCC000000.toInt() else color)

        // 绘制按钮边框
        context.fill(x, y, x + width, y + 2, 0xFFFFFFFF.toInt())
        context.fill(x, y + height - 2, x + width, y + height, 0xFFFFFFFF.toInt())
        context.fill(x, y, x + 2, y + height, 0xFFFFFFFF.toInt())
        context.fill(x + width - 2, y, x + width, y + height, 0xFFFFFFFF.toInt())

        // 绘制按钮文本
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val textWidth = textRenderer.getWidth(text)
        val textX = x + (width - textWidth) / 2
        val textY = y + (height - textRenderer.fontHeight) / 2
        context.drawText(textRenderer, text, textX, textY, Color.WHITE.rgb, false)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        // 检查是否点击了保存按钮
        if (mouseX >= saveButtonX && mouseX <= saveButtonX + buttonWidth &&
            mouseY >= saveButtonY && mouseY <= saveButtonY + buttonHeight) {
            saveYamlContent()
            return true
        }

        // 检查是否点击了退出按钮
        if (mouseX >= exitButtonX && mouseX <= exitButtonX + buttonWidth &&
            mouseY >= exitButtonY && mouseY <= exitButtonY + buttonHeight) {
            close()
            return true
        }

        // 检查是否点击了重置按钮
        if (mouseX >= resetButtonX && mouseX <= resetButtonX + buttonWidth &&
            mouseY >= resetButtonY && mouseY <= resetButtonY + buttonHeight) {
            yamlContent = originalContent
            return true
        }

        // 检查是否点击了编辑区域
        if (mouseX >= editorX && mouseX <= editorX + editorWidth &&
            mouseY >= editorY && mouseY <= editorY + editorHeight) {
            val textRenderer = MinecraftClient.getInstance().textRenderer
            val lineHeight = textRenderer.fontHeight + 2
            val clickedLine = ((mouseY - editorY - 5) / lineHeight).toInt()
            val lineIndex = scrollOffset + clickedLine
            val lines = yamlContent.split("\n".toRegex())

            if (lineIndex >= 0 && lineIndex < lines.size) {
                cursorLine = lineIndex
                val line = lines[lineIndex]
                val clickedX = (mouseX - editorX - 5).toInt()

                // 计算光标列位置
                cursorColumn = 0
                var currentX = 0
                for (i in line.indices) {
                    val charWidth = textRenderer.getWidth(line[i].toString())
                    if (currentX + charWidth / 2 > clickedX) break
                    currentX += charWidth
                    cursorColumn++
                }

                cursorVisible = true
                cursorBlinkTimer = 0f
            }
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun saveYamlContent() {
        try {
            val configFile = Paths.get("config/iannjhclient/modules.yaml").toFile()
            configFile.writeText(yamlContent)

            // 重新加载配置
            ConfigManager.loadConfig()

            // 更新原始内容
            originalContent = yamlContent

            // 显示保存成功消息
            MinecraftClient.getInstance().player?.sendMessage(Text.of("配置已保存并重新加载"))
        } catch (e: Exception) {
            e.printStackTrace()
            MinecraftClient.getInstance().player?.sendMessage(Text.of("保存配置失败: ${e.message}"))
        }
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        // 如果鼠标在编辑区域内，则滚动内容
        if (mouseX >= editorX && mouseX <= editorX + editorWidth &&
            mouseY >= editorY && mouseY <= editorY + editorHeight) {
            scrollOffset -= (verticalAmount * 3).toInt()
            if (scrollOffset < 0) {
                scrollOffset = 0
            }
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        // 在光标位置插入字符
        val lines = yamlContent.split("\n".toRegex()).toMutableList()
        if (cursorLine >= 0 && cursorLine < lines.size) {
            val line = lines[cursorLine].toMutableList()
            line.add(cursorColumn.coerceAtMost(line.size), chr)
            lines[cursorLine] = line.joinToString("")
            cursorColumn++
            yamlContent = lines.joinToString("\n")
        }
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {

        val lines = yamlContent.split("\n".toRegex()).toMutableList()

        when (keyCode) {
            259 -> { // Backspace键
                if (cursorLine >= 0 && cursorLine < lines.size) {
                    val line = lines[cursorLine].toMutableList()
                    if (cursorColumn > 0) {
                        line.removeAt(cursorColumn - 1)
                        cursorColumn--
                        lines[cursorLine] = line.joinToString("")
                    } else if (cursorLine > 0) {
                        // 合并到上一行
                        val prevLine = lines[cursorLine - 1]
                        cursorColumn = prevLine.length
                        lines[cursorLine - 1] = prevLine + lines[cursorLine]
                        lines.removeAt(cursorLine)
                        cursorLine--
                    }
                    yamlContent = lines.joinToString("\n")
                }
                return true
            }
            257 -> { // Enter键
                if (cursorLine >= 0 && cursorLine < lines.size) {
                    val line = lines[cursorLine]
                    val beforeCursor = line.substring(0, cursorColumn)
                    val afterCursor = line.substring(cursorColumn)
                    lines[cursorLine] = beforeCursor
                    lines.add(cursorLine + 1, afterCursor)
                    cursorLine++
                    cursorColumn = 0
                    yamlContent = lines.joinToString("\n")
                }
                return true
            }
            263 -> { // 左箭头
                if (cursorColumn > 0) {
                    cursorColumn--
                } else if (cursorLine > 0) {
                    cursorLine--
                    cursorColumn = lines[cursorLine].length
                }
                return true
            }
            262 -> { // 右箭头
                if (cursorLine < lines.size && cursorColumn < lines[cursorLine].length) {
                    cursorColumn++
                } else if (cursorLine < lines.size - 1) {
                    cursorLine++
                    cursorColumn = 0
                }
                return true
            }
            265 -> { // 上箭头
                if (cursorLine > 0) {
                    cursorLine--
                    cursorColumn = cursorColumn.coerceAtMost(lines[cursorLine].length)
                }
                return true
            }
            264 -> { // 下箭头
                if (cursorLine < lines.size - 1) {
                    cursorLine++
                    cursorColumn = cursorColumn.coerceAtMost(lines[cursorLine].length)
                }
                return true
            }
            268 -> { // Home键
                cursorColumn = 0
                return true
            }
            269 -> { // End键
                if (cursorLine >= 0 && cursorLine < lines.size) {
                    cursorColumn = lines[cursorLine].length
                }
                return true
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}