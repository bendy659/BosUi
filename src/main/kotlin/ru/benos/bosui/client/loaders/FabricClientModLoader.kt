//? if fabric {
package ru.benos.bosui.client.loaders

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import ru.benos.bosui.client.BosUiClient

object FabricClientModLoader : ClientModInitializer, IModLoader {
    override val name: String = "Fabric"
    override val isDevelopment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment
    override val isClient: Boolean
        get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT

    override fun isModLoaded(modId: String): Boolean =
        FabricLoader.getInstance().isModLoaded(modId)

    override fun onInitializeClient() =
        BosUiClient.initClient(this)
}
//? }
