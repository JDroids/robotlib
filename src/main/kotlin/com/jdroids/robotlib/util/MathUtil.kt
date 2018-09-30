package com.jdroids.robotlib.util

/**
 * A singleton that contains a bunch of math lambdas
 */
object MathUtil {
    val clamp = {value: Double, low: Double, high: Double -> Math.max(low, Math.min(value, high))}
}