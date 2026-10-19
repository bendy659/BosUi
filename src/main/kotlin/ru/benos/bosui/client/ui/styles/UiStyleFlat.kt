package ru.benos.bosui.client.ui.styles

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.primitives.UiRect
import java.awt.Insets

open class UiStyleFlat : AbstractUiStyleBox {
    var backgroundColor: UiColor
    var backgroundMargin: Insets
    var outlineWidth: Insets
    var outlineColor: UiColor

    constructor(
        contentPadding: Insets, contentClip: Boolean,
        backgroundColor: UiColor, backgroundMargin: Insets,
        outlineWidth: Insets, outlineColor: UiColor
    ) : super(contentPadding, contentClip) {
        this.backgroundColor = backgroundColor
        this.backgroundMargin = backgroundMargin
        this.outlineWidth = outlineWidth
        this.outlineColor = outlineColor
    }
    constructor() : this(
        Insets(0, 0, 0, 0), false,
        UiColor.BLACK_25, Insets(0, 0, 0, 0),
        Insets(0, 0, 0, 0), UiColor.TRANSPARENT
    )

    open fun backgroundColor(color: UiColor) { this.backgroundColor = color }
    open fun backgroundColor(hex: Int) { this.backgroundColor = UiColor.fromHex(hex) }
    open fun backgroundColor(hex: Long) { this.backgroundColor = UiColor.fromHex(hex) }
    open fun backgroundColor(r: Int, g: Int, b: Int, a: Int = 255) { this.backgroundColor = UiColor(r, g, b, a) }
    open fun backgroundColor(r: Float, g: Float, b: Float, a: Float) { this.backgroundColor = UiColor(r, g, b, a) }

    open fun backgroundMargin(insets: Insets) { this.backgroundMargin = insets }
    open fun backgroundMargin(left: Int, top: Int, right: Int, bottom: Int) { this.backgroundMargin = Insets(left, top, right, bottom) }

    open fun outlineWidth(insets: Insets) { this.outlineWidth = insets }
    open fun outlineWidth(left: Int, top: Int, right: Int, bottom: Int) { this.outlineWidth = Insets(left, top, right, bottom) }

    open fun outlineColor(color: UiColor) { this.outlineColor = color }
    open fun outlineColor(hex: Int) { this.outlineColor = UiColor.fromHex(hex) }
    open fun outlineColor(hex: Long) { this.outlineColor = UiColor.fromHex(hex) }
    open fun outlineColor(r: Int, g: Int, b: Int, a: Int = 255) { this.outlineColor = UiColor(r, g, b, a) }
    open fun outlineColor(r: Float, g: Float, b: Float, a: Float) { this.outlineColor = UiColor(r, g, b, a) }

    override fun render(graphics: GuiGraphicsExtractor, bound: UiRect, body: () -> Unit) {
        if (!hasSizeAndColor(bound, backgroundMargin, backgroundColor)) return
        val bgRect = bound.margin(backgroundMargin)

        // Draw background //
        graphics.fill(RenderPipelines.GUI, bgRect.x, bgRect.y, bgRect.right, bgRect.bottom, backgroundColor.int)

        // Try draw outline //
        if (hasInsetsAndColor(outlineWidth, outlineColor)) {
            val color = outlineColor.int
            val a = bgRect.x - outlineWidth.left
            val b = bgRect.y - outlineWidth.top
            val c = bgRect.right + outlineWidth.right

            /* left */ graphics.fill(a, b, bgRect.x, bgRect.bottom, color)
            /* top */ graphics.fill(a, b, c, bgRect.y, color)
            /* right */ graphics.fill(bgRect.right, bgRect.y, c, bgRect.bottom, color)
            /* bottom */ graphics.fill(a, bgRect.bottom, c, bgRect.bottom + outlineWidth.bottom, color)
        }

        if (contentClip) graphics.clipOfBound(this.contentBound(bound), body)
        else body()
    }
}