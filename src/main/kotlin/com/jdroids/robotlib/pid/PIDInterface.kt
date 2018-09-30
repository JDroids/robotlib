package com.jdroids.robotlib.pid

import com.qualcomm.robotcore.hardware.PIDCoefficients
import kotlin.math.abs

interface PIDInterface {
    var input: () -> Double

    var output: ((Double) -> Unit)

    var result: Double

    var pidCoefficients: PIDCoefficients

    var v: Double

    var a: Double

    var setpoint: Double

    fun reset()

    class Tolerance(var check: Double, var onTarget: (Double) -> Boolean) {
        companion object {
            fun nullTolerance(): Tolerance = Tolerance (0.0) {
                throw IllegalStateException("No tolerance value set when calling onTarget().")
            }
            fun percentageTolerance(percentage: Double): Tolerance = Tolerance(percentage) {
                error -> error < percentage / 100
            }

            fun absoluteTolerance(value: Double): Tolerance =
                    Tolerance(value) {error -> abs(error) < value}
        }
    }
}