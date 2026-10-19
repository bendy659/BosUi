package ru.benos.bosui.client.ui.nodes

import ru.benos.bosui.client.ui.AbstractBosUI

/** Store all helper functions-builders for fast, easy and short build [AbstractBosUI.nodeTree] */
object Nodes {
    fun UiNode.Node(nodeName: String? = null, body: UiNode.() -> Unit) {
        val node = UiNode()
        nodeName?.let { node.nodeName = it }
        node.body()

        this@Node.addChild(node)
    }

    /** Helper, for easy create builder for [UiControlNode] */
    fun UiNode.Control(nodeName: String? = null, body: UiControlNode.() -> Unit) {
        val node = UiControlNode()
        nodeName?.let { node.nodeName = it }
        node.body()

        this@Control.addChild(node)
    }

    fun UiNode.Panel(nodeName: String? = null, body: UiPanelNode.() -> Unit) {
        val node = UiPanelNode()
        nodeName?.let { node.nodeName = it }
        node.body()

        this@Panel.addChild(node)
    }

    fun UiNode.Label(nodeName: String? = null, body: UiLabelNode.() -> Unit) {
        val node = UiLabelNode()
        nodeName?.let { node.nodeName = it }
        node.body()

        this@Label.addChild(node)
    }
}