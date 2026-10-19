package ru.benos.bosui.client.ui.styles

import net.minecraft.client.gui.GuiGraphicsExtractor
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.clip
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.primitives.UiRect
import java.awt.Insets

abstract class AbstractUiStyleBox {
    var contentPadding: Insets
    var contentClip: Boolean

    constructor(contentPadding: Insets, contentClip: Boolean) {
        this.contentPadding = contentPadding
        this.contentClip = contentClip
    }

    open fun contentPadding(insets: Insets) { this.contentPadding = insets }
    open fun contentPadding(left: Int, top: Int, right: Int, bottom: Int) { this.contentPadding = Insets(left, top, right, bottom) }

    open fun contentClip(bool: Boolean) { this.contentClip = bool }

    open fun render(graphics: GuiGraphicsExtractor, bound: UiRect, body: () -> Unit) { /* Nothing */ }
    open fun contentBound(bound: UiRect): UiRect =
        bound.padding(contentPadding)

    open fun hasInsetsAndColor(insets: Insets, color: UiColor): Boolean {
        val c0 = insets.left > 0
        val c1 = insets.top > 0
        val c2 = insets.right > 0
        val c3 = insets.bottom > 0
        val c4 = color.int != 0

        return c0 && c1 && c2 && c3 && c4
    }

    open fun hasSizeAndColor(bound: UiRect, insets: Insets, color: UiColor): Boolean {
        val padded = bound.padding(insets)
        val c0 = padded.width > 0
        val c1 = padded.height > 0
        val c2 = color.int != 0

        return c0 && c1 && c2
    }

    open fun GuiGraphicsExtractor.clipOfBound(bound: UiRect, body: () -> Unit) {
        val bound = this@AbstractUiStyleBox.contentBound(bound)
        val (x0, y0) = bound.x to bound.y
        val (x1, y1) = bound.right to bound.bottom

        this@clipOfBound.clip(x0, y0, x1, y1, body)
    }
}