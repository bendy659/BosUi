package ru.benos.bosui.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.benos.boslib.helpers.BosLibIdentifierHelper.identifier
import ru.benos.bosui.client.loaders.IModLoader

object BosUiClient {
    const val MOD_ID: String = "bosui"
    const val MOD_NAME: String = "BosUi"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    lateinit var LOADER: IModLoader

    val keybindCategory: KeyMapping.Category = KeyMapping.Category("${MOD_ID}:keybinds".identifier)
    val OPEN_DEMO_UI: KeyMapping = KeyMapping("open_demo_ui", InputConstants.KEY_G, keybindCategory)

    fun initClient(loader: IModLoader) { LOADER = loader;  LOGGER.info("Initialized $MOD_NAME on ${loader.name}") }
}