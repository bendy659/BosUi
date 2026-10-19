package ru.benos.bosui.client.primitives

import net.minecraft.resources.Identifier
import kotlin.math.roundToInt

data class UiTexture(
    var identifier: Identifier,
    var x: Float, var y: Float,
    var width: Float, var height: Float,
    var srcWidth: Int, var srcHeight: Int
) {
    constructor(identifier: Identifier, x: Int, y: Int, width: Int, height: Int, srcWidth: Int, srcHeight: Int) : this(identifier, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), srcWidth, srcHeight)
    constructor(identifier: Identifier, x: Float, y: Float, width: Float, height: Float) : this(identifier, x, y, width, height, width.roundToInt(), height.roundToInt())
    constructor(identifier: Identifier, x: Int, y: Int, width: Int, height: Int) : this(identifier, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), width, height)
    constructor(identifier: Identifier, width: Float, height: Float) : this(identifier, 0.0f, 0.0f, width, height, width.roundToInt(), height.roundToInt())
    constructor(identifier: Identifier, width: Int, height: Int): this(identifier, 0.0f, 0.0f, width.toFloat(), height.toFloat(), width, height)
}
