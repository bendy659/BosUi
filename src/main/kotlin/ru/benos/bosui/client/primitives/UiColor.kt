package ru.benos.bosui.client.primitives

import kotlin.math.roundToInt

data class UiColor(var r: Float, var g: Float, var b: Float, var a: Float = 1.0f) {
    constructor(r: Int, g: Int, b: Int, a: Int = 255) :
            this(r.toFloat() / 255.0f, g.toFloat() / 255.0f, b.toFloat() / 255.0f, a.toFloat() / 255.0f)
    constructor() : this(1.0f, 1.0f, 1.0f)

    companion object {
        val TRANSPARENT: UiColor get() = UiColor(0.0f, 0.0f, 0.0f, 0.0f)

        val BLACK: UiColor get() = gray(0.0f)
        val BLACK_25: UiColor get() = gray(0.0f, 0.25f)
        val BLACK_50: UiColor get() = gray(0.0f, 0.5f)
        val BLACK_75: UiColor get() = gray(0.0f, 0.75f)

        val WHITE: UiColor get() = gray(1.0f)
        val WHITE_25: UiColor get() = gray(1.0f, 0.25f)
        val WHITE_50: UiColor get() = gray(1.0f, 0.5f)
        val WHITE_75: UiColor get() = gray(1.0f, 0.75f)

        fun fromHex(hex: Long): UiColor {
            val r = (hex shr 16 and 0xFF).toInt()
            val g = (hex shr 8 and 0xFF).toInt()
            val b = (hex and 0xFF).toInt()
            val a = (hex shr 24 and 0xFF).toInt()

            return UiColor(r, g, b, a)
        }

        fun fromHex(hex: Int): UiColor {
            val r = hex shr 16 and 0xFF
            val g = hex shr 8 and 0xFF
            val b = hex and 0xFF

            return UiColor(r, g, b)
        }

        fun gray(w: Float, a: Float = 1.0f): UiColor =
            UiColor(w, w, w, a)
    }

    val int: Int
        get() {
            val aInt = (a.coerceIn(0f, 1f) * 255f).roundToInt()
            val rInt = (r.coerceIn(0f, 1f) * 255f).roundToInt()
            val gInt = (g.coerceIn(0f, 1f) * 255f).roundToInt()
            val bInt = (b.coerceIn(0f, 1f) * 255f).roundToInt()
            return (aInt shl 24) or (rInt shl 16) or (gInt shl 8) or bInt
        }

    val long: Long
        get() = int.toLong() and 0xFFFFFFFFL

    fun brightness(w: Float) {
        fun Float.clamped(): Float =
            Math.clamp(this@clamped, 0.0f, 1.0f)

        this.r = (r + w).clamped()
        this.g = (g + w).clamped()
        this.b = (b + w).clamped()
    }
}
