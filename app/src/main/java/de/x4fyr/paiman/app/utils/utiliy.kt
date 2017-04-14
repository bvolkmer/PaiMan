package de.x4fyr.paiman.app.utils

import javafx.geometry.Point2D
import javafx.scene.Node
import tornadofx.*

/**
 * Simple 2-dimensional vector with some mathematical operators implemented
 */
class Vector(
        /** first dimension */
        var x: Double,
        /** second dimension */
        var y: Double) {
    constructor(origin: Point2D?, destination: Point2D)
            : this(destination.x - (origin?.x ?: 0.0), destination.y - (origin?.y ?: 0.0))

    /** representation as a json array */
    override fun toString(): String = "[$x, $y]"

    /** vector plus */
    operator fun plus(o: Vector) = Vector(x + o.x, y + o.y)

    /** vector minus */
    operator fun minus(o: Vector) = Vector(x - o.x, y - o.y)

    /** vector multiplication */
    operator fun times(o: Vector) = Vector(x * o.x, y * o.y)

    /** vector multiplication  with number */
    operator fun times(o: Double) = Vector(x * o, y * o)

    /** vector division with number */
    operator fun div(o: Double) = Vector(x / o, y / o)

    companion object {
        /** zero value vector */
        val ZERO = Vector(0.0, 0.0)
    }
}


/** Stub Decorator with empty implementations */
class StubDecorator : Decorator {
    /** Empty implementation */
    override fun decorate(node: Node) {
    }

    /** Empty implementation */
    override fun undecorate(node: Node) {
    }
}