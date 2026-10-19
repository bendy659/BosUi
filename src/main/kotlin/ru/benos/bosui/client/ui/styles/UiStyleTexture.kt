package ru.benos.bosui.client.ui.styles

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.blitTiled
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.drawNineSliceTexture
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.primitives.UiRect
import ru.benos.bosui.client.primitives.UiTexture
import java.awt.Insets

open class UiStyleTexture : AbstractUiStyleBox {
    var backgroundTexture: UiTexture?
    var backgroundTextureMargin: Insets
    var backgroundTextureTiling: Boolean
    var backgroundMargin: Insets
    var backgroundColor: UiColor

    constructor(
        contentPadding: Insets,
        contentClip: Boolean,
        backgroundTexture: UiTexture?,
        backgroundTextureMargin: Insets,
        backgroundTextureTiling: Boolean,
        backgroundMargin: Insets,
        backgroundColor: UiColor
    ) : super(contentPadding, contentClip) {
        this.backgroundTexture = backgroundTexture
        this.backgroundTextureMargin = backgroundTextureMargin
        this.backgroundTextureTiling = backgroundTextureTiling
        this.backgroundMargin = backgroundMargin
        this.backgroundColor = backgroundColor
    }
    constructor() : this(
        Insets(0, 0, 0, 0),
        false,
        null,
        Insets(0, 0, 0, 0),
        false,
        Insets(0, 0, 0, 0),
        UiColor.TRANSPARENT
    )

    open fun backgroundTexture(uiTexture: UiTexture?) { this.backgroundTexture = uiTexture }
    open fun backgroundTexture(identifier: Identifier, x: Float, y: Float, width: Float, height: Float) { this.backgroundTexture = UiTexture(identifier, x, y, width, height) }
    open fun backgroundTexture(identifier: Identifier, x: Int, y: Int, width: Int, height: Int) { this.backgroundTexture = UiTexture(identifier, x, y, width, height) }
    open fun backgroundTexture(identifier: Identifier, x: Float, y: Float, width: Float, height: Float, srcWidth: Int, srcHeight: Int) { this.backgroundTexture = UiTexture(identifier, x, y, width, height, srcWidth, srcHeight) }
    open fun backgroundTexture(identifier: Identifier, x: Int, y: Int, width: Int, height: Int, srcWidth: Int, srcHeight: Int) { this.backgroundTexture = UiTexture(identifier, x, y, width, height, srcWidth, srcHeight) }

    open fun backgroundTextureMargin(insets: Insets) { this.backgroundMargin = insets }
    open fun backgroundTextureMargin(left: Int, top: Int, right: Int, bottom: Int) { this.backgroundMargin = Insets(left, top, right, bottom) }

    open fun backgroundTextureTiling(bool: Boolean) { this.backgroundTextureTiling = bool }

    open fun backgroundMargin(insets: Insets) { this.backgroundMargin = insets }
    open fun backgroundMargin(left: Int, top: Int, right: Int, bottom: Int) { this.backgroundMargin = Insets(left, top, right, bottom) }

    open fun backgroundColor(color: UiColor) { this.backgroundColor = color }
    open fun backgroundColor(hex: Int) { this.backgroundColor = UiColor.fromHex(hex) }
    open fun backgroundColor(hex: Long) { this.backgroundColor = UiColor.fromHex(hex) }
    open fun backgroundColor(r: Int, g: Int, b: Int, a: Int = 255) { this.backgroundColor = UiColor(r, g, b, a) }
    open fun backgroundColor(r: Float, g: Float, b: Float, a: Float) { this.backgroundColor = UiColor(r, g, b, a) }

    override fun render(graphics: GuiGraphicsExtractor, bound: UiRect, body: () -> Unit) {
        val texture = backgroundTexture
        if (texture != null && hasSizeAndColor(bound, backgroundMargin, backgroundColor)) {
            val bgRect = bound.margin(backgroundMargin)

            if (backgroundTextureMargin != Insets(0, 0, 0, 0)) {
                graphics.drawNineSliceTexture(
                    RenderPipelines.GUI_TEXTURED, texture.identifier,
                    bgRect.x, bgRect.y, bgRect.width, bgRect.height,
                    texture.x, texture.y, texture.width, texture.height,
                    backgroundTextureMargin.left, backgroundTextureMargin.top, backgroundTextureMargin.right, backgroundTextureMargin.bottom,
                    backgroundTextureTiling,
                    texture.srcWidth, texture.srcHeight
                )
            } else {
                if (backgroundTextureTiling)
                    graphics.blitTiled(
                        RenderPipelines.GUI_TEXTURED, texture.identifier,
                        bgRect.x, bgRect.y, bgRect.width, bgRect.height,
                        texture.x, texture.y,
                        texture.width.toInt(), texture.height.toInt(),
                        backgroundColor.int
                    )
                else
                    graphics.blit(
                        RenderPipelines.GUI_TEXTURED, texture.identifier,
                        bgRect.x, bgRect.y,
                        texture.x, texture.y,
                        bgRect.width, bgRect.height,
                        texture.width.toInt(), texture.height.toInt(),
                        backgroundColor.int
                    )
            }
        }

        if (contentClip) graphics.clipOfBound(contentBound(bound), body)
        else body()
    }
}