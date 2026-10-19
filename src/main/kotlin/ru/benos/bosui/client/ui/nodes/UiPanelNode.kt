package ru.benos.bosui.client.ui.nodes

import net.minecraft.client.gui.GuiGraphicsExtractor
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.transform
import ru.benos.bosui.client.ui.styles.*

/**
 * Renderable widget with own style in this node
 */
open class UiPanelNode : UiControlNode {
    private var panelStyleBox: AbstractUiStyleBox = UiStyleEmpty()

    constructor(nodeName: String) : super(nodeName)
    constructor() : super()

    open fun style(panelStyleBox: AbstractUiStyleBox) { this.panelStyleBox = panelStyleBox }
    open fun styleEmpty(panelStyleEmptyBox: UiStyleEmpty.() -> Unit) { this.panelStyleBox = Styles.Empty(panelStyleEmptyBox) }
    open fun styleFlat(panelStyleFlatBox: UiStyleFlat.() -> Unit) { this.panelStyleBox = Styles.Flat(panelStyleFlatBox) }
    open fun styleTexture(panelStyleTextureBox: UiStyleTexture.() -> Unit) { this.panelStyleBox = Styles.Texture(panelStyleTextureBox) }

    override fun processNode(delta: Float) { this.panelStyleBox.contentBound(this.nodeBound) }

    override fun renderChildren(graphics: GuiGraphicsExtractor) =
        this.panelStyleBox.render(graphics, this.contentBound) {
            super.renderChildren(graphics)
        }
}