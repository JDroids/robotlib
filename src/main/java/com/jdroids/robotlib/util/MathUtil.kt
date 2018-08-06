package com.jdroids.robotlib.util

import kotlin.math.PI

/**
 * A singleton that contains a bunch of stuff necessary for math.
 */
object MathUtil {
    fun limit(value: Double, limit: Double): Double =
            if (Math.abs(value) > limit) limit * Math.signum(value) else value

    //Constants
    const val sq2p1 = 2.414213562373095048802e0
    const val sq2m1 = .414213562373095048802e0
    const val p4 = .161536412982230228262e2
    const val p3 = .26842548195503973794141e3
    const val p2 = .11530293515404850115428136e4
    const val p1 = .178040631643319697105464587e4
    const val p0 = .89678597403663861959987488e3
    const val q4 = .5895697050844462222791e2
    const val q3 = .536265374031215315104235e3
    const val q2 = .16667838148816337184521798e4
    const val q1 = .207933497444540981287275926e4
    const val q0 = .89678597403663861962481162e3
    const val PIO2 = 1.5707963267948966135E0
    val nan = Double.NaN

    val Math.TAU: Double
        get() = PI * 2

    private fun mxatan(arg: Double): Double {
        val argsq = arg * arg
        val value: Double

        value = ((((p4 * argsq + p3) * argsq + p2) * argsq + p1) * argsq + p0) /
                (((((argsq + q4) * argsq + q3) * argsq + q2) * argsq + q1) * argsq + q0)

        return value * arg
    }

    private fun msatan(arg: Double): Double = when {
        arg < sq2m1 -> mxatan(arg)
        arg > sq2p1 -> PIO2 - mxatan(1 / arg)
        else -> PIO2 / 2 + mxatan((arg - 1) / (arg + 1))
    }

    //Implementation of atan
    fun atan(arg: Double): Double = if (arg > 0) msatan(arg) else -msatan(-arg)

    //Implementation of atan2
    fun atan2(arg1: Double, arg2: Double): Double {
        var arg1 = arg1
        if (arg1 + arg2 == arg1) {
            return if (arg1 >= 0) {
                PIO2
            } else -PIO2
        }
        arg1 = atan(arg1 / arg2)
        return if (arg2 < 0) {
            if (arg1 <= 0) {
                arg1 + Math.PI
            }
            else {
                arg1 - Math.PI
            }
        }
        else {
            arg1
        }
    }

    //Implementation of asin
    fun asin(arg: Double): Double {
        var arg = arg
        var temp: Double
        var sign = 0

        if (arg < 0) {
            arg = -arg
            ++sign
        }
        if (arg > 1) {
            return nan
        }
        temp = Math.sqrt(1 - arg * arg)
        if (arg > 0.7) {
            temp = PIO2 - atan(temp/arg)
        }
        else {
            temp = atan(arg/temp)
        }
        if (sign > 0) {
            temp = -temp
        }

        return temp
    }

    //Implementation of acos
    fun acos(arg: Double): Double = if (arg > 1 || arg < -1) nan else PIO2 - asin(arg)

    /**
     * Get the difference in angle between two angles.
     *
     * @param from The first angle
     * @param to The second angle
     * @return The change in angle from the first argument necessary to line up
     * with the second. Always between -Pi and Pi
     */
    fun getDifferenceInAngleRadians(from: Double, to: Double): Double {
        return boundAngleNegPiToPiRadians(to - from)
    }

    /**
     * Get the difference in angle between two angles.
     *
     * @param from The first angle
     * @param to The second angle
     * @return The change in angle from the first argument necessary to line up
     * with the second. Always between -180 and 180
     */
    fun getDifferenceInAngleDegrees(from: Double, to: Double): Double {
        return boundAngleNeg180to180Degrees(to - from)
    }

    fun boundAngle0to360Degrees(angle: Double): Double {
        var angle = angle
        // Naive algorithm
        while (angle >= 360.0) {
            angle -= 360.0
        }
        while (angle < 0.0) {
            angle += 360.0
        }
        return angle
    }

    fun boundAngleNeg180to180Degrees(angle: Double): Double {
        var angle = angle
        // Naive algorithm
        while (angle >= 180.0) {
            angle -= 360.0
        }
        while (angle < -180.0) {
            angle += 360.0
        }
        return angle
    }

    fun boundAngle0to2PiRadians(angle: Double): Double {
        var angle = angle
        // Naive algorithm
        while (angle >= 2.0 * Math.PI) {
            angle -= 2.0 * Math.PI
        }
        while (angle < 0.0) {
            angle += 2.0 * Math.PI
        }
        return angle
    }

    fun boundAngleNegPiToPiRadians(angle: Double): Double {
        var angle = angle
        // Naive algorithm
        while (angle >= Math.PI) {
            angle -= 2.0 * Math.PI
        }
        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI
        }
        return angle
    }
}