package ru.benos.bosui.client.primitives

import org.joml.Vector2i
import java.awt.Insets

/**
 * One of the primitives for 'BosUI' library, stored [x], [y], [width] and [height] data's
 * @param x first stored data. Store value of horizontal dimensional
 * @param y second stored data. Store value of vertical dimensional
 * @param width third stored data. Store value of scale horizontal dimensional
 * @param height fourth stored data. Store value of scale vertical dimensional
 */
data class UiRect(var x: Int, var y: Int, var width: Int, var height: Int) {
    /** Helper, for get top-left point position in this rectangle */
    val x0: Vector2i get() = Vector2i(x, y)

    /** Helper, for get top-right point position in this rectangle */
    val y0: Vector2i get() = Vector2i(x + width, y)

    /** Helper, for get bottom-left point position in this rectangle */
    val x1: Vector2i get() = Vector2i(x, y + height)

    /** Helper, for get bottom-right point position in this rectangle */
    val y1: Vector2i get() = Vector2i(x + width, y + height)

    val right: Int get() = x + width
    val bottom: Int get() = y + height

    var position: Vector2i
        get() = Vector2i(x, y)
        set(v) { this.x = v.x; this.y = v.y }
    var size: Vector2i
        get() = Vector2i(width, height)
        set(v) { this.width = v.x; this.height = v.y }

    /** Default constructor for get clean ZERO data */
    constructor(position: Vector2i, size: Vector2i) : this(position.x, position.y, size.x, size.y)
    constructor() : this(0, 0, 0, 0)

    fun margin(insets: Insets): UiRect =
        UiRect(this.x + insets.left, this.y + insets.top, width - insets.top, height - insets.bottom)
    fun margin(left: Int, top: Int, right: Int, bottom: Int): UiRect =
        this.margin(Insets(left, top, right, bottom))

    fun padding(insets: Insets): UiRect =
        UiRect(this.x - insets.left, this.y - insets.top, width + insets.top, height + insets.bottom)
    fun padding(left: Int, top: Int, right: Int, bottom: Int): UiRect =
        this.padding(Insets(left, top, right, bottom))
}
