package ru.benos.bosui.client.ui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import ru.benos.bosui.client.primitives.UiAlign
import ru.benos.bosui.client.primitives.UiColor
import ru.benos.bosui.client.ui.nodes.Nodes.Control
import ru.benos.bosui.client.ui.nodes.Nodes.Label
import ru.benos.bosui.client.ui.nodes.Nodes.Panel
import ru.benos.bosui.client.ui.nodes.UiControlNode
import ru.benos.bosui.client.ui.nodes.UiLabelNode
import ru.benos.bosui.client.ui.nodes.UiLabelNode.WordWrapMode
import ru.benos.bosui.client.ui.nodes.UiNode
import ru.benos.bosui.client.ui.nodes.UiPanelNode
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * BosUI Showcase Demo Screen.
 * Demonstrates Godot-like SceneTree node hierarchy, UI panels, label typography,
 * word wrapping (BY_WORDS, BY_SYMBOLS, DISABLE with suffix), text alignment,
 * procedural animation, and real-time telemetry.
 */
class BosUiDemo : AbstractBosUI {
    private val winW = 410
    private val winH = 246

    private var totalTime: Float = 0f
    private var lastMouseX: Float = 0f
    private var lastMouseY: Float = 0f

    // Cached node references for fast process loop
    private var windowNode: UiControlNode? = null
    private var fpsLabel: UiLabelNode? = null
    private var deltaLabel: UiLabelNode? = null
    private var uptimeLabel: UiLabelNode? = null
    private var nodesLabel: UiLabelNode? = null
    private var cursorLabel: UiLabelNode? = null
    private var pulseLabel: UiLabelNode? = null
    private var progressFill: UiPanelNode? = null
    private var progressVal: UiLabelNode? = null
    private var sineDot: UiPanelNode? = null

    // FPS smoothing
    private var frameCount: Int = 0
    private var fpsTimer: Float = 0f
    private var currentFps: Int = 60

    constructor() : super(410, 246)

    override fun init() {
        super.init()
        // Reset node cache on screen resize / re-init
        windowNode = null
        fpsLabel = null
        deltaLabel = null
        uptimeLabel = null
        nodesLabel = null
        cursorLabel = null
        pulseLabel = null
        progressFill = null
        progressVal = null
        sineDot = null
    }

    override fun UiNode.buildTree() =
        Control("window") {
            position(0, 0)
            size(winW, winH)

            // Outer Background Panel
            Panel("background") {
                size(winW, winH)
                styleFlat { backgroundColor(0.08f, 0.10f, 0.14f, 0.96f) }
            }

            // --- Header Bar ---
            Panel("header") {
                position(0, 0)
                size(winW, 26)
                styleFlat { backgroundColor(0.14f, 0.17f, 0.24f, 1.0f) }

                Label("header_title") {
                    position(10, 0)
                    size(winW - 20, 26)
                    align(UiAlign.LEFT, UiAlign.CENTER)
                    label("§b§lBosUI §7| §fSceneTree Showcase")
                    fontSettings { fontSize(16) }
                }

                Label("header_badge") {
                    position(10, 0)
                    size(winW - 20, 26)
                    align(UiAlign.RIGHT, UiAlign.CENTER)
                    label("§a● ACTIVE")
                    fontSettings { fontSize(16) }
                }
            }

            // --- Left Column: Telemetry & Inspector ---
            Panel("telemetry_card") {
                position(10, 34)
                size(190, 178)
                styleFlat { backgroundColor(0.11f, 0.13f, 0.18f, 0.85f) }

                Label("telemetry_title") {
                    position(8, 6)
                    label("§6§l[ TELEMETRY ]")
                    fontSettings { fontSize(16) }
                }

                Label("fps_label") {
                    position(8, 22)
                    label("§7FPS: §a60")
                    fontSettings { fontSize(16) }
                }

                Label("delta_label") {
                    position(8, 36)
                    label("§7Delta: §f16 ms")
                    fontSettings { fontSize(16) }
                }

                Label("uptime_label") {
                    position(8, 50)
                    label("§7Uptime: §f0.0s")
                    fontSettings { fontSize(16) }
                }

                Label("nodes_label") {
                    position(8, 64)
                    label("§7Total Nodes: §e--")
                    fontSettings { fontSize(16) }
                }

                Label("cursor_label") {
                    position(8, 78)
                    label("§7Cursor: §f0, 0")
                    fontSettings { fontSize(16) }
                }

                // Interactive Sine Oscillation Sub-panel
                Label("osc_title") {
                    position(8, 98)
                    label("§3Sine Oscillator:")
                    fontSettings { fontSize(16) }
                }

                Panel("sine_track") {
                    position(8, 114)
                    size(174, 26)
                    styleFlat { backgroundColor(0.06f, 0.07f, 0.10f, 1.0f) }

                    Panel("sine_dot") {
                        position(76, 6)
                        size(14, 14)
                        styleFlat { backgroundColor(0.2f, 0.85f, 1.0f, 1.0f) }
                    }
                }

                Label("osc_hint") {
                    position(8, 146)
                    size(174, 14)
                    align(UiAlign.CENTER, UiAlign.TOP)
                    label("§8Procedural Math.sin(t)")
                    fontSettings { fontSize(14) }
                }
            }

            // --- Right Column: Typography & Word Wrap Showcase ---
            Panel("components_card") {
                position(210, 34)
                size(190, 178)
                styleFlat { backgroundColor(0.11f, 0.13f, 0.18f, 0.85f) }

                Label("comp_title") {
                    position(8, 6)
                    label("§d§l[ TYPOGRAPHY & WRAP ]")
                    fontSettings { fontSize(16) }
                }

                // Drop-shadow showcase
                Label("shadow_demo") {
                    position(8, 22)
                    label("Drop-Shadow Label")
                    fontSettings {
                        fontColor(UiColor.WHITE)
                        shadow(1.0f, 1.0f, UiColor.BLACK_50)
                        fontSize(16)
                    }
                }

                // Multi-line word-wrapped label showcase (BY_WORDS)
                Label("wrap_demo") {
                    position(8, 38)
                    size(174, 30)
                    wordWrap(WordWrapMode.BY_WORDS)
                    label("§7Word-wrap flows text automatically across bounded lines.")
                    fontSettings {
                        fontSize(14)
                        lineSpace(1.1f)
                    }
                }

                // Truncated label showcase (DISABLE with suffix)
                Label("truncated_demo") {
                    position(8, 72)
                    size(174, 14)
                    wordWrap(WordWrapMode.DISABLE)
                    wordWrapSuffix("...")
                    label("§8Very long label text that gets truncated automatically with suffix")
                    fontSettings { fontSize(14) }
                }

                // Pulsing dynamic star label
                Label("pulse_label") {
                    position(8, 92)
                    size(174, 16)
                    align(UiAlign.CENTER, UiAlign.TOP)
                    label("★ Dynamic Pulse ★")
                    fontSettings {
                        fontColor(0xFFD700)
                        shadow(0.5f, 0.5f, UiColor.BLACK_50)
                        fontSize(16)
                    }
                }

                // Animated Progress Bar Widget
                Panel("progress_track") {
                    position(8, 118)
                    size(174, 14)
                    styleFlat { backgroundColor(0.06f, 0.07f, 0.10f, 1.0f) }

                    Panel("progress_fill") {
                        position(0, 0)
                        size(0, 14)
                        styleFlat { backgroundColor(0.25f, 0.75f, 1.0f, 1.0f) }
                    }
                }

                Label("progress_val") {
                    position(8, 138)
                    size(174, 14)
                    align(UiAlign.CENTER, UiAlign.TOP)
                    label("§7Cycle: §b0%")
                    fontSettings { fontSize(14) }
                }
            }

            // --- Footer Bar ---
            Panel("footer") {
                position(0, winH - 24)
                size(winW, 24)
                styleFlat { backgroundColor(0.06f, 0.07f, 0.10f, 0.95f) }

                Label("footer_text") {
                    position(0, 0)
                    size(winW, 24)
                    align(UiAlign.CENTER, UiAlign.CENTER)
                    label("§8[ESC] §7Close  §8|  [G] §7Toggle  §8|  §3BosUI SceneTree v1.0")
                    fontSettings { fontSize(14) }
                }
            }
        }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super.mouseMoved(mouseX, mouseY)
        this.lastMouseX = mouseX.toFloat()
        this.lastMouseY = mouseY.toFloat()
    }

    override fun process(delta: Float) {
        totalTime += delta

        // Update FPS counter
        frameCount++
        fpsTimer += delta
        if (fpsTimer >= 0.5f) {
            currentFps = (frameCount / fpsTimer).roundToInt()
            frameCount = 0
            fpsTimer = 0f
        }

        // Cache nodes
        val window = windowNode ?: this.nodeTree.getChildOfName<UiControlNode>("window")?.also { windowNode = it }
        val fps = fpsLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("fps_label")?.also { fpsLabel = it }
        val dLabel = deltaLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("delta_label")?.also { deltaLabel = it }
        val uptime = uptimeLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("uptime_label")?.also { uptimeLabel = it }
        val nodes = nodesLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("nodes_label")?.also { nodesLabel = it }
        val cursor = cursorLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("cursor_label")?.also { cursorLabel = it }
        val pulse = pulseLabel ?: this.nodeTree.getChildOfName<UiLabelNode>("pulse_label")?.also { pulseLabel = it }
        val fill = progressFill ?: this.nodeTree.getChildOfName<UiPanelNode>("progress_fill")?.also { progressFill = it }
        val progVal = progressVal ?: this.nodeTree.getChildOfName<UiLabelNode>("progress_val")?.also { progressVal = it }
        val dot = sineDot ?: this.nodeTree.getChildOfName<UiPanelNode>("sine_dot")?.also { sineDot = it }

        // Telemetry values
        val deltaMs = (delta * 1000f).roundToInt()
        fps?.label("§7FPS: §a$currentFps")
        dLabel?.label("§7Delta: §f$deltaMs ms")
        uptime?.label("§7Uptime: §f${(totalTime * 10).toInt() / 10.0}s")
        nodes?.label("§7Total Nodes: §e${countNodes(this.nodeTree)}")
        cursor?.label("§7Cursor: §f(${virtualMouseX.toInt()}, ${virtualMouseY.toInt()})")

        // Progress bar procedural animation
        val progress = (sin(totalTime * 1.6) * 0.5 + 0.5).toFloat()
        val fillWidth = (progress * 174f).coerceIn(0f, 174f)
        fill?.size(fillWidth, 14)
        progVal?.label("§7Cycle: §b${(progress * 100).toInt()}%")

        // Sine wave dot oscillation (174 - 26 = 148 range)
        val dotX = 6f + ((sin(totalTime * 2.8) * 0.5 + 0.5) * 148f).toFloat()
        dot?.position(dotX, 6)

        // Pulsing star animation
        val star = if (((totalTime * 2.5).toInt() % 2) == 0) "★" else "☆"
        pulse?.label("§e$star Dynamic Pulse $star")
    }

    override fun render(graphics: GuiGraphicsExtractor) {
        // Subtle dark backdrop overlay behind modal window
        graphics.fill(RenderPipelines.GUI, 0, 0, this.width, this.height, 0x88000000.toInt())
        super.render(graphics)
    }

    private fun countNodes(node: UiNode): Int {
        var count = 1
        for (child in node.children())
            count += countNodes(child)

        return count
    }
}