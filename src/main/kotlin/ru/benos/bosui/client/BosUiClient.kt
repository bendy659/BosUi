package ru.benos.bosui.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.benos.bosui.client.loaders.IModLoader

object BosUiClient {
    const val MOD_ID: String = "bosui"
    const val MOD_NAME: String = "BosUi"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    lateinit var LOADER: IModLoader

    fun initClient(loader: IModLoader) { LOADER = loader; LOGGER.info("Initialized $MOD_NAME on ${loader.name}") }
}