package ru.benos.bosui.client.ui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import org.joml.Vector2f
import ru.benos.boslib.client.helpers.BosScreenHelper
import ru.benos.boslib.helpers.BosLibGuiGraphicsExtractorHelper.transform
import ru.benos.boslib.helpers.BosLibMath.f
import ru.benos.bosui.client.ui.nodes.UiNode

/**
 * Abstract node
 * Used for [UiNode]
 */
abstract class AbstractBosUI : BosScreenHelper {
    private var _deltaTime: Float = 0f
    private var lastFrameTimeNs: Long = 0L

    protected lateinit var nodeTree: UiNode
    protected var viewportSize: Vector2f

    constructor(title: Component, viewportSizeX: Float, viewportSizeY: Float) :
            super(title) { this.viewportSize = Vector2f(viewportSizeX, viewportSizeY) }
    constructor(title: Component, viewportSizeX: Int, viewportSizeY: Int) :
            this(title, viewportSizeX.toFloat(), viewportSizeY.toFloat())
    constructor(viewportSizeX: Int, viewportSizeY: Int) :
            this(Component.empty(), viewportSizeX, viewportSizeY)
    constructor(viewportSizeX: Float, viewportSizeY: Float) :
            this(Component.empty(), viewportSizeX, viewportSizeY)
    constructor() : this(Component.empty(), 640, 480)

    open fun viewportSize(x: Number, y: Number) { this.viewportSize = Vector2f(x.f, y.f) }
    open fun viewportSize(size: Vector2f) { this.viewportSize = size }

    open val viewportScale: Float
        get() {
            if (this.viewportSize.x <= 0f || this.viewportSize.y <= 0f) return 1.0f
            val scaleX = this.width.toFloat() / this.viewportSize.x
            val scaleY = this.height.toFloat() / this.viewportSize.y
            return minOf(scaleX, scaleY)
        }

    open val viewportOffset: Vector2f
        get() {
            if (this.viewportSize.x <= 0f || this.viewportSize.y <= 0f) return Vector2f()
            val s = viewportScale
            val ox = (this.width.toFloat() - this.viewportSize.x * s) / 2f
            val oy = (this.height.toFloat() - this.viewportSize.y * s) / 2f
            return Vector2f(ox, oy)
        }

    var virtualMouseX: Float = 0f
        protected set
    var virtualMouseY: Float = 0f
        protected set

    open fun toVirtualMouse(screenX: Double, screenY: Double): Vector2f {
        val s = viewportScale
        val offset = viewportOffset
        return Vector2f((screenX.toFloat() - offset.x) / s, (screenY.toFloat() - offset.y) / s)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super.mouseMoved(mouseX, mouseY)
        val vm = toVirtualMouse(mouseX, mouseY)
        this.virtualMouseX = vm.x
        this.virtualMouseY = vm.y
    }

    abstract fun UiNode.buildTree()

    open fun process(delta: Float) { /* Nothing */ }

    override fun init() {
        super.init()
        lastFrameTimeNs = 0L
    }

    override fun render(graphics: GuiGraphicsExtractor) {
        tryInitNodeTree()

        calcDelta()
        process(this._deltaTime)
        nodeTree.processNode(this._deltaTime)
        nodeTree.processChildren(this._deltaTime)

        val s = viewportScale
        val offset = viewportOffset
        val scaleVec = Vector2f(s, s)

        graphics.transform(offset, null, scaleVec, null) {
            nodeTree.renderNode(graphics) // Render this node
            nodeTree.renderChildren(graphics) // Render all child node of this node
        }
    }

    private fun tryInitNodeTree() {
        if (this::nodeTree.isInitialized) return

        this.nodeTree = UiNode()
        this.nodeTree.buildTree()
    }

    protected open fun calcDelta(): Float {
        val now = System.nanoTime()
        if (lastFrameTimeNs == 0L) {
            lastFrameTimeNs = now
            this._deltaTime = 0.0f
            return 0.0f
        }

        val elapsed = ((now - lastFrameTimeNs) / 1_000_000_000.0).toFloat()
        this.lastFrameTimeNs = now
        this._deltaTime = elapsed.coerceIn(0f, 0.1f)

        return this._deltaTime
    }
}