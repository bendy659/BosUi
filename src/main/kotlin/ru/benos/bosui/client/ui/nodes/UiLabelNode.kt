package ru.benos.bosui.client.ui.nodes

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import org.joml.Vector2f
import ru.benos.boslib.helpers.BosLibComponentHelper.literal
import ru.benos.boslib.helpers.BosLibComponentHelper.translatable
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.scale
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.transform
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.translate
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.bosui.client.primitives.UiAlign
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.ui.UiFontSettings

open class UiLabelNode : UiControlNode {
    enum class WordWrapMode { DISABLE, BY_SYMBOLS, BY_WORDS }

    protected var label: Component? = null
    protected var fontSettings: UiFontSettings = UiFontSettings()
    protected var wordWrap: WordWrapMode = WordWrapMode.DISABLE
    protected var wordWrapSuffix: String = "..."

    constructor(nodeName: String) : super(nodeName)
    constructor() : super()

    open fun label(label: Component) { this.label = label }
    open fun label(label: String) { this.label = label.literal }
    open fun label(label: String, vararg args: Any) { this.label = label.literal(*args) }
    open fun translatable(label: String) { this.label = label.translatable }
    open fun translatable(label: String, vararg args: Any) { this.label = label.translatable(args) }

    open fun wordWrap(mode: WordWrapMode) { this.wordWrap = mode }
    open fun wordWrapSuffix(suffix: String) { this.wordWrapSuffix = suffix }

    open fun fontSettings(): UiFontSettings = this.fontSettings
    open fun fontSettings(settings: UiFontSettings) { this.fontSettings = settings }
    open fun fontSettings(body: UiFontSettings.() -> Unit) = this.fontSettings.body()

    override fun renderNode(graphics: GuiGraphicsExtractor) {
        val curLabel = this.label ?: return
        val curFont = fontSettings.font ?: Minecraft.getInstance().font

        val fontScale = fontSettings.fontSize.toFloat() / UiFontSettings.DEFAULT_FONT_SIZE.toFloat()
        val availableWidth = if (this.size.x > 0f) this.size.x / fontScale else null

        val lines = fontSettings.wrapLines(curLabel, availableWidth, wordWrap, wordWrapSuffix, curFont)
        val lineHeight = fontSettings.lineHeight(curFont)
        val totalTextHeight = lines.size * lineHeight

        graphics.transform(position, rotation, scale, pivot) {
            val drawBody = {
                var curY = when (verticalAlign) {
                    UiAlign.CENTER -> (this.size.y - totalTextHeight) / 2f
                    UiAlign.BOTTOM -> this.size.y - totalTextHeight
                    else -> 0f
                }
                if (this.size.y <= 0f || curY < 0f) curY = 0f

                lines.forEach { line ->
                    val lineWidth = fontSettings.lineWidth(line, curFont)
                    var curX = when (horizontalAlign) {
                        UiAlign.CENTER -> (this.size.x - lineWidth) / 2f
                        UiAlign.RIGHT -> this.size.x - lineWidth
                        else -> 0f
                    }
                    if (this.size.x <= 0f || curX < 0f) curX = 0f

                    val lineComp = line.toComponent()

                    // Try draw shadow
                    if (fontSettings.shadowColor != UiColor.TRANSPARENT &&
                        (fontSettings.shadowOffset.x != 0f || fontSettings.shadowOffset.y != 0f)
                    ) {
                        graphics.translate(fontSettings.shadowOffset) {
                            graphics.text(
                                curFont, lineComp,
                                curX.toInt(), curY.toInt(),
                                fontSettings.shadowColor.int
                            )
                        }
                    }

                    // Draw text line
                    graphics.text(curFont, lineComp, curX.toInt(), curY.toInt(), fontSettings.fontColor.int)

                    curY += lineHeight
                }
            }

            if (fontScale != 1.0f) {
                graphics.scale(fontScale.f, fontScale.f, null, null) {
                    drawBody()
                }
            } else {
                drawBody()
            }
        }
    }
}