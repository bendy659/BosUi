package ru.benos.bosui.client.ui

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import org.joml.Vector2f
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.scale
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.translate
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.ui.nodes.UiLabelNode
import java.util.Optional

data class UiFontSettings(
    var font: Font? = null,
    var fontSize: Int = 16,
    var fontColor: UiColor = UiColor.WHITE,
    var shadowOffset: Vector2f = Vector2f(),
    var shadowColor: UiColor = UiColor.TRANSPARENT,
    var highlight: Boolean = false,
    var charSpace: Float = 1.0f,
    var wordSpace: Float = 1.0f,
    var lineSpace: Float = 1.0f
) {
    data class StyledChar(val char: Char, val style: Style) {
        fun toComponent(): Component =
            Component.literal(char.toString()).withStyle(style)
    }

    data class StyledWord(val chars: List<StyledChar>, val leadingSpaces: Int = 0) {
        fun toComponent(): Component {
            val root = Component.empty()
            chars.forEach { root.append(it.toComponent()) }

            return root
        }
    }

    data class StyledLine(val words: List<StyledWord>) {
        fun toComponent(): Component {
            val root = Component.empty()
            words.forEach { word ->
                repeat(word.leadingSpaces) { root.append(" ") }
                root.append(word.toComponent())
            }

            return root
        }
    }

    companion object { const val DEFAULT_FONT_SIZE: Int = 16 }

    fun font(font: Font) { this.font = font }

    fun fontSize(size: Int) { this.fontSize = size }

    fun fontColor(color: UiColor) { this.fontColor = color }
    fun fontColor(hex: Int) { this.fontColor = UiColor.fromHex(hex) }
    fun fontColor(hex: Long) { this.fontColor = UiColor.fromHex(hex) }
    fun color(color: UiColor) = fontColor(color)
    fun color(hex: Int) = fontColor(hex)
    fun color(hex: Long) = fontColor(hex)

    fun shadowOffset(shadowOffset: Vector2f) { this.shadowOffset = shadowOffset }
    fun shadowOffset(x: Float, y: Float) { this.shadowOffset = Vector2f(x, y) }
    fun shadow(x: Float, y: Float, color: UiColor) {
        this.shadowOffset = Vector2f(x, y)
        this.shadowColor = color
    }
    fun shadow(x: Float, y: Float, hex: Int) {
        this.shadowOffset = Vector2f(x, y)
        this.shadowColor = UiColor.fromHex(hex)
    }
    fun shadow(x: Float, y: Float, hex: Long) {
        this.shadowOffset = Vector2f(x, y)
        this.shadowColor = UiColor.fromHex(hex)
    }
    fun shadow(shadowOffset: Vector2f, shadowColor: UiColor) {
        this.shadowOffset = shadowOffset
        this.shadowColor = shadowColor
    }
    fun shadow(offsetX: Number, offsetY: Number, shadowColor: UiColor) {
        this.shadowOffset = Vector2f(offsetX.f, offsetY.f)
        this.shadowColor = shadowColor
    }

    fun shadowColor(color: UiColor) { this.shadowColor = color }
    fun shadowColor(hex: Int) { this.shadowColor = UiColor.fromHex(hex) }
    fun shadowColor(hex: Long) { this.shadowColor = UiColor.fromHex(hex) }

    fun highlight(highlight: Boolean) { this.highlight = highlight }

    fun charSpace(charSpace: Float) { this.charSpace = charSpace }
    fun wordSpace(wordSpace: Float) { this.wordSpace = wordSpace }
    fun lineSpace(lineSpace: Float) { this.lineSpace = lineSpace }

    fun parse(label: Component): List<StyledLine> {
        val allChars = mutableListOf<StyledChar>()
        label.visit({ style, text ->
            var currentStyle = style
            var i = 0
            while (i < text.length) {
                val ch = text[i]
                if (ch == '§' && i + 1 < text.length) {
                    val format = ChatFormatting.getByCode(text[i + 1])
                    if (format != null) {
                        currentStyle = currentStyle.applyLegacyFormat(format)
                        i += 2
                        continue
                    }
                }
                allChars.add(StyledChar(ch, currentStyle))
                i++
            }
            Optional.empty<Unit>()
        }, Style.EMPTY)

        val lines = mutableListOf<StyledLine>()
        var currentWords = mutableListOf<StyledWord>()
        var currentChars = mutableListOf<StyledChar>()
        var pendingSpaces = 0

        fun finishWord() {
            if (currentChars.isNotEmpty()) {
                currentWords.add(StyledWord(currentChars, pendingSpaces))
                currentChars = mutableListOf()
                pendingSpaces = 0
            }
        }

        fun finishLine() {
            finishWord()
            lines.add(StyledLine(currentWords))
            currentWords = mutableListOf()
            pendingSpaces = 0
        }

        for (sc in allChars) {
            when (sc.char) {
                '\r' -> continue
                '\n' -> finishLine()
                ' ' -> {  finishWord(); pendingSpaces++ }
                else -> currentChars.add(sc)
            }
        }
        finishLine()

        return lines
    }

    fun spaceWidth(font: Font): Float = font.width(" ") * wordSpace

    fun lineHeight(font: Font): Float = font.lineHeight * lineSpace

    fun wordWidth(word: StyledWord, font: Font): Float =
        word.chars.sumOf { font.width(it.char.toString()).toDouble() }.toFloat() * charSpace

    fun lineWidth(line: StyledLine, font: Font): Float {
        val sw = spaceWidth(font)
        var total = 0f
        line.words.forEach { word ->
            total += word.leadingSpaces * sw
            word.chars.forEach { total += font.width(it.char.toString()) * charSpace }
        }
        return total
    }

    /**
     * Splits label into lines according to available width and [UiLabelNode.WordWrapMode].
     */
    fun wrapLines(
        label: Component,
        availableWidth: Number? = null,
        mode: UiLabelNode.WordWrapMode = UiLabelNode.WordWrapMode.DISABLE,
        suffix: String = "...",
        font: Font? = null
    ): List<StyledLine> {
        val curFont = font ?: this.font
        val logicalLines = parse(label)
        val maxWidth = availableWidth?.toFloat()?.takeIf { it > 0f } ?: return logicalLines

        if (mode == UiLabelNode.WordWrapMode.DISABLE) {
            if (suffix.isEmpty() || curFont == null) return logicalLines

            val sw = spaceWidth(curFont)
            val suffixWidth = curFont.width(suffix) * charSpace

            return logicalLines.map { line ->
                val fullLineWidth = lineWidth(line, curFont)
                if (fullLineWidth <= maxWidth) {
                    line
                } else {
                    val truncatedWords = mutableListOf<StyledWord>()
                    var currentW = 0f
                    var done = false

                    for (word in line.words) {
                        val spaceW = word.leadingSpaces * sw
                        if (currentW + spaceW + suffixWidth > maxWidth && truncatedWords.isNotEmpty()) {
                            done = true
                            break
                        }
                        currentW += spaceW

                        val truncatedChars = mutableListOf<StyledChar>()
                        for (ch in word.chars) {
                            val chW = curFont.width(ch.char.toString()) * charSpace
                            if (currentW + chW + suffixWidth > maxWidth) {
                                done = true
                                break
                            }
                            currentW += chW
                            truncatedChars.add(ch)
                        }

                        if (truncatedChars.isNotEmpty()) {
                            truncatedWords.add(StyledWord(truncatedChars, word.leadingSpaces))
                        }
                        if (done) break
                    }

                    val suffixChars = suffix.map { StyledChar(it, Style.EMPTY) }
                    if (truncatedWords.isNotEmpty()) {
                        val lastWord = truncatedWords.removeAt(truncatedWords.size - 1)
                        truncatedWords.add(StyledWord(lastWord.chars + suffixChars, lastWord.leadingSpaces))
                    } else {
                        truncatedWords.add(StyledWord(suffixChars, 0))
                    }
                    StyledLine(truncatedWords)
                }
            }
        }

        if (curFont == null) return logicalLines

        val sw = spaceWidth(curFont)
        val wrappedLines = mutableListOf<StyledLine>()

        if (mode == UiLabelNode.WordWrapMode.BY_WORDS) {
            for (line in logicalLines) {
                var currentLineWords = mutableListOf<StyledWord>()
                var currentLineWidth = 0f

                for (word in line.words) {
                    val wWidth = wordWidth(word, curFont)
                    val leadingSpaceW = word.leadingSpaces * sw
                    val neededWidth = if (currentLineWords.isEmpty()) wWidth else currentLineWidth + leadingSpaceW + wWidth

                    if (neededWidth <= maxWidth) {
                        currentLineWords.add(word)
                        currentLineWidth = neededWidth
                    } else {
                        if (currentLineWords.isNotEmpty()) {
                            wrappedLines.add(StyledLine(currentLineWords))
                            currentLineWords = mutableListOf()
                            currentLineWidth = 0f
                        }

                        if (wWidth <= maxWidth) {
                            currentLineWords.add(StyledWord(word.chars, 0))
                            currentLineWidth = wWidth
                        } else {
                            var subChars = mutableListOf<StyledChar>()
                            var subWidth = 0f

                            for (ch in word.chars) {
                                val chW = curFont.width(ch.char.toString()) * charSpace
                                if (subWidth + chW > maxWidth && subChars.isNotEmpty()) {
                                    wrappedLines.add(StyledLine(listOf(StyledWord(subChars, 0))))
                                    subChars = mutableListOf()
                                    subWidth = 0f
                                }
                                subChars.add(ch)
                                subWidth += chW
                            }
                            if (subChars.isNotEmpty()) {
                                currentLineWords.add(StyledWord(subChars, 0))
                                currentLineWidth = subWidth
                            }
                        }
                    }
                }
                if (currentLineWords.isNotEmpty()) {
                    wrappedLines.add(StyledLine(currentLineWords))
                }
            }
        } else if (mode == UiLabelNode.WordWrapMode.BY_SYMBOLS) {
            for (line in logicalLines) {
                var currentLineWords = mutableListOf<StyledWord>()
                var currentWordChars = mutableListOf<StyledChar>()
                var currentLineWidth = 0f
                var pendingLeadingSpaces = 0

                fun finishWord() {
                    if (currentWordChars.isNotEmpty()) {
                        currentLineWords.add(StyledWord(currentWordChars, pendingLeadingSpaces))
                        currentWordChars = mutableListOf()
                        pendingLeadingSpaces = 0
                    }
                }

                fun finishLine() {
                    finishWord()
                    if (currentLineWords.isNotEmpty()) {
                        wrappedLines.add(StyledLine(currentLineWords))
                        currentLineWords = mutableListOf()
                        currentLineWidth = 0f
                    }
                }

                for (word in line.words) {
                    val leadingW = word.leadingSpaces * sw
                    if (currentLineWidth + leadingW > maxWidth && currentLineWidth > 0f) {
                        finishLine()
                    } else {
                        currentLineWidth += leadingW
                        pendingLeadingSpaces += word.leadingSpaces
                    }

                    for (ch in word.chars) {
                        val chW = curFont.width(ch.char.toString()) * charSpace
                        if (currentLineWidth + chW > maxWidth && currentLineWidth > 0f) {
                            finishLine()
                        }
                        currentWordChars.add(ch)
                        currentLineWidth += chW
                    }
                    finishWord()
                }
                finishLine()
            }
        }

        return if (wrappedLines.isEmpty()) logicalLines else wrappedLines
    }

    /**
     * Returns a list of [Component] lines ready to render.
     */
    fun wrap(
        label: Component,
        availableWidth: Number? = null,
        mode: UiLabelNode.WordWrapMode = UiLabelNode.WordWrapMode.DISABLE,
        suffix: String = "...",
        font: Font? = null
    ): List<Component> =
        wrapLines(label, availableWidth, mode, suffix, font).map { it.toComponent() }

    fun measure(graphics: GuiGraphicsExtractor, label: Component): Vector2f {
        val curFont = this.font ?: Minecraft.getInstance().font
        val lines = parse(label)

        val spaceWidth = curFont.width(" ") * wordSpace
        val lineHeight = curFont.lineHeight * lineSpace

        var maxWidth = 0f
        lines.forEach { line ->
            var lineWidth = 0f
            line.words.forEach { word ->
                lineWidth += word.leadingSpaces * spaceWidth
                word.chars.forEach { sc ->
                    lineWidth += curFont.width(sc.char.toString()) * charSpace
                }
            }
            if (lineWidth > maxWidth) maxWidth = lineWidth
        }

        val totalHeight = lines.size * lineHeight
        val fontScale = fontSize.toFloat() / DEFAULT_FONT_SIZE.toFloat()
        return Vector2f(maxWidth * fontScale, totalHeight * fontScale)
    }

    fun measure(
        label: Component,
        availableWidth: Number? = null,
        mode: UiLabelNode.WordWrapMode = UiLabelNode.WordWrapMode.DISABLE,
        font: Font? = null
    ): Vector2f {
        val curFont = font ?: this.font ?: return Vector2f()
        val lines = wrapLines(label, availableWidth, mode, font = curFont)
        val lineHeight = lineHeight(curFont)

        var maxWidth = 0f
        lines.forEach { line ->
            val lw = lineWidth(line, curFont)
            if (lw > maxWidth) maxWidth = lw
        }

        val totalHeight = lines.size * lineHeight
        val fontScale = fontSize.toFloat() / DEFAULT_FONT_SIZE.toFloat()
        return Vector2f(maxWidth * fontScale, totalHeight * fontScale)
    }
}
