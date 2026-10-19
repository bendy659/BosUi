package ru.benos.bosui.client.ui.nodes

import net.minecraft.client.gui.GuiGraphicsExtractor
import org.joml.Vector2f
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.transform
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.boslib.helpers.BosLibVector2.toVector2f
import ru.benos.boslib.helpers.BosLibVector2.toVector2i
import ru.benos.bosui.client.primitives.UiAlign
import ru.benos.bosui.client.primitives.UiRect

/**
 * Basic renderable node
 * @param nodeBound is bound box of node
 * @param contentBound is bound for content box of node
 *
 * @param rotation visual rotation of node. Not resizing [nodeBound]
 * @param pivot pivot-point node in global position of screen
 * @param position helper, for change node position in one call
 * @param offset helper, for offset current node position in one call
 */
open class UiControlNode : UiNode {
    protected open var nodeBound: UiRect
        get() = UiRect(this.position.toVector2i, this.size.toVector2i)
        set(v) { this.position = v.position.toVector2f; this.size = v.size.toVector2f }
    protected open var contentBound: UiRect
        get() = nodeBound
        set(v) { /* Nothing */ }

    protected var position: Vector2f = Vector2f()
    protected var rotation: Float = 0.0f
    protected var size: Vector2f = Vector2f()
    protected var pivot: Vector2f = Vector2f()
    protected var scale: Vector2f = Vector2f(1.0f, 1.0f)

    protected var horizontalAlign: UiAlign = UiAlign.LEFT
    protected var expandHorizontalAlign: Boolean = false
    protected var verticalAlign: UiAlign = UiAlign.TOP
    protected var expandVerticalAlign: Boolean = false

    constructor(nodeName: String) : super(nodeName)
    constructor() : super()

    /**
     * Helper, for change node position in two-dimensional
     * @param x horizontal dimension
     * @param y vertical dimension
     */
    open fun position(x: Number, y: Number) { this.position = Vector2f(x.f, y.f) }

    /**
     * Helper, for changing position in two-dimensional in one time
     * @param xy horizontal and vertical dimensional
     */
    open fun position(xy: Number) { this.position(xy, xy) }

    open fun size(width: Number, height: Number) { this.size = Vector2f(width.f, height.f) }

    open fun size(wh: Number) { this.size(wh, wh) }

    open fun pivot(x: Number, y: Number) { this.pivot = Vector2f(x.f, y.f) }

    open fun pivot(xy: Number) { this.pivot = Vector2f(xy.f) }

    open fun scale(width: Number, height: Number) { this.scale = Vector2f(width.f, height.f) }

    open fun scale(wh: Number) { this.scale(wh, wh) }

    open fun align(horizontal: UiAlign, vertical: UiAlign) {
        this.horizontalAlign = horizontal
        this.verticalAlign = vertical
    }
    open fun horizontalAlign(align: UiAlign) { this.horizontalAlign = align }
    open fun verticalAlign(align: UiAlign) { this.verticalAlign = align }

    /**
     * Helper, for offset current node position in two-dimensional
     * @param x horizontal dimension
     * @param y vertical dimension
     * @param withPivot need in also offset and a [pivot] position
     */
    open fun offset(x: Number, y: Number, withPivot: Boolean = true) {
        this.position.x += x.f; this.position.y += y.f;
        pivot.x += x.f; pivot.y += y.f
    }

    /**
     * Helper, for offset current node position in two-dimensional in one time
     * @param xy horizontal and vertical offset dimensional
     */
    open fun offset(xy: Number) { this.offset(xy, xy) }

    override fun renderChildren(graphics: GuiGraphicsExtractor) =
        graphics.transform(this.position, this.rotation, this.scale, this.pivot) { super.renderChildren(graphics) }
}