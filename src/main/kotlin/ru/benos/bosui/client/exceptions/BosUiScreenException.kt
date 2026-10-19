package ru.benos.bosui.client.exceptions

import ru.benos.bosui.client.BosUiClient

class BosUiScreenException : BosUiException {
    constructor(message: String, exception: Exception) :
            super("%s/Screen".format(BosUiClient.MOD_NAME), message, exception)
}