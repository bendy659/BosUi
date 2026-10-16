//? if neoforge {
/*package ru.benos.bosui.client.loaders

import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import ru.benos.bosui.client.BosUiClient

@Mod(BosUiClient.MOD_ID)
object NeoForgeModLoader : IModLoader {
    override val name: String = "NeoForge"
    override val isDevelopment: Boolean
        get() = !FMLEnvironment.isProduction()
    override val isClient: Boolean
        get() = FMLEnvironment.getDist().isClient

    override fun isModLoaded(modId: String): Boolean =
        ModList.get().isLoaded(modId)

    init { if (isClient) BosUiClient.initClient(this) }
}
*///? }
