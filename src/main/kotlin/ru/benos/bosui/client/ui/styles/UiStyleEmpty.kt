package ru.benos.bosui.client.ui.styles

import net.minecraft.client.gui.GuiGraphicsExtractor
import ru.benos.bosui.client.primitives.UiRect
import java.awt.Insets

class UiStyleEmpty : AbstractUiStyleBox {
    constructor(contentMargin: Insets, contentClip: Boolean = false) : super(contentMargin, contentClip)
    constructor() : this(Insets(0, 0, 0, 0))

    override fun render(graphics: GuiGraphicsExtractor, bound: UiRect, body: () -> Unit) =
        graphics.clipOfBound(bound, body)
}