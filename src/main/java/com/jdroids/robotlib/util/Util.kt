package com.jdroids.robotlib.util

object Util {
    fun limit(value: Double, limit: Double): Double =
            if (Math.abs(value) > limit) limit * Math.signum(value) else value
}