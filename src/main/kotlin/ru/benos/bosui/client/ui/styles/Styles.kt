package ru.benos.bosui.client.ui.styles

object Styles {
    fun Empty(body: UiStyleEmpty.() -> Unit): UiStyleEmpty {
        val style = UiStyleEmpty()
        style.body()

        return style
    }

    fun Flat(body: UiStyleFlat.() -> Unit): UiStyleFlat {
        val style = UiStyleFlat()
        style.body()

        return style
    }

    fun Texture(body: UiStyleTexture.() -> Unit): UiStyleTexture {
        val style = UiStyleTexture()
        style.body()

        return style
    }
}