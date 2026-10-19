package ru.benos.bosui.client.ui.nodes

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen

/**
 * This is an empty base class for other nodes.
 * @param parent Setup parent for this node. He can be nullable.
 * @param children Store child nodes in this node.
 */
open class UiNode {
    var nodeName: String

    private var parent: UiNode? = null
    private var children: MutableMap<String, UiNode> = mutableMapOf()

    constructor(nodeName: String) { this.nodeName = nodeName }
    constructor() { this.nodeName = "" }

    /** Get parent from this node */
    open fun parent(): UiNode = this.parent ?: this

    /** Get list child-nodes from this node */
    open fun children(): List<UiNode> = this.children.values.toList()

    /** Calling when node is added to treeNode */
    open fun readyNode() { /* Nothing */ }

    /** Calling when node is removed from treeNode */
    open fun goodbyeNode() { /* Nothing */ }

    /**
     * Calling in every frame in render for this node, before [renderNode]
     * @param delta difference between previous and next frames
     */
    open fun processNode(delta: Float) { /* Nothing */ }

    /**
     * Calling in every frame in render for this node, after [processNode]
     * @param graphics [Screen] render context for this frame
     */
    open fun renderNode(graphics: GuiGraphicsExtractor) { /* This is not a renderable node */ }

    /**
     * Calling for every child before [processNode] child node in every frames
     * @param node child node
     * @param delta difference between previous and next frames
     */
    protected open fun processChildNode(node: UiNode, delta: Float) =
        node.processChildren(delta)

    /**
     * Calling for every child before [renderNode] child node in every frames
     * @param node child node
     * @param graphics [Screen] render context for this frame
     */
    protected open fun renderChildNode(node: UiNode, graphics: GuiGraphicsExtractor) =
        node.renderChildren(graphics)

    /**
     * Calling in every frame in render for this node, before [renderChildren]
     * First call [processNode] for this node, after for every children in [processChildNode]
     * @param delta difference between previous and next frames
     */
    open fun processChildren(delta: Float): Unit =
        this.children.forEach { (_, childNode) -> childNode.processNode(delta); processChildNode(childNode, delta) }

    /**
     * Calling in every frame in render for this node, after [processChildren]
     * First call [renderNode] for this node, after for every children in [renderChildNode]
     * @param graphics [Screen] render context for this frame
     */
    open fun renderChildren(graphics: GuiGraphicsExtractor): Unit =
        this.children.forEach { (_, childNode) -> childNode.renderNode(graphics); renderChildNode(childNode, graphics) }

    /**
     * Add child to this parent node. After add - call [readyNode] from child node
     * @param node child node
     */
    open fun addChild(node: UiNode) {
        val uniqueName = generateChildNodeName(node)
        node.nodeName = uniqueName

        this.children[uniqueName] = node
        node.parent = this
        node.readyNode()
    }

    /**
     * Generate unique name for a child node within this parent
     */
    protected open fun generateChildNodeName(node: UiNode): String {
        if (node.nodeName.isNotEmpty() && node.nodeName !in this.children) {
            return node.nodeName
        }

        val baseName = node.nodeName.ifEmpty { node::class.simpleName ?: "UiNode" }
        var index = 0
        var generated = "${baseName}_$index"
        while (generated in this.children) {
            index++
            generated = "${baseName}_$index"
        }
        return generated
    }

    /**
     * Remove child to this parent node. After remove - call [goodbyeNode] from child node
     * @param node child node
     */
    open fun removeChild(node: UiNode) {
        this.children.remove(node.nodeName);
        node.parent = null
        node.goodbyeNode()
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T: UiNode> getChildOfName(childName: String, recursive: Boolean = true): T? {
        val direct = this.children[childName] as? T
        if (direct != null) return direct
        if (!recursive) return null

        for (child in this.children.values) {
            val found = child.getChildOfName<T>(childName, recursive = true)
            if (found != null) return found
        }
        return null
    }

    /**
     * Get node by hierarchy path (e.g. "UiControlNode_0/panel") like Godot get_node
     */
    @Suppress("UNCHECKED_CAST")
    open fun <T: UiNode> getNode(path: String): T? {
        if (!path.contains('/')) return getChildOfName(path)
        var current: UiNode? = this
        for (part in path.split('/')) {
            if (part.isEmpty()) continue
            current = current?.getChildOfName(part, recursive = false)
        }
        return current as? T
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T: UiNode> getChildOfIndex(childIndex: Int): T? =
        this.children()[childIndex] as? T
}