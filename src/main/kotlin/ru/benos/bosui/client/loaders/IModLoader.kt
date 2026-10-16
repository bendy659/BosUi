package ru.benos.bosui.client.loaders

interface IModLoader {
    val name: String
    val isDevelopment: Boolean
    val isClient: Boolean

    fun isModLoaded(modId: String): Boolean
}
