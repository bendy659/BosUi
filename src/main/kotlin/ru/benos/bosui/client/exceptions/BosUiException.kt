package ru.benos.bosui.client.exceptions

import ru.benos.bosui.client.BosUiClient

open class BosUiException : Exception {
    constructor(prefix: String, message: String, exception: Exception) :
            super("[%s | Exception] %s\n%s".format(prefix, message, exception.message))
    constructor(message: String, exception: Exception) :
            super("[%s | Exception] %s\n%s".format(message, exception.message))
}