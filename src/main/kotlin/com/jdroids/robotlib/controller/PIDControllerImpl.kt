package com.jdroids.robotlib.controller

import kotlin.math.max

/**
 * This is a simple implementation of [PIDController].
 *
 * @param input a lambda providing the current state of the system
 * @param output a lambda to do something with the given output
 * @param setpoint the setpoint that the controller is trying to get to
 * @param p the p coefficient
 * @param i the i coefficient
 * @param d the d coefficient
 */
class PIDControllerImpl(override var input: () -> Double,
                        override var output: (Double) -> Unit,
                        override var setpoint: Double,
                        override var p: Double,
                        override var i: Double,
                        override var d: Double) : PIDController {

    private var lastTime = System.nanoTime() * 1_000_000_000

    private var errorSum = 0.0
    var lastError = 0.0

    private var minInput: Double? = null
    private var maxInput: Double? = null

    /**
     * Calculates the result of the controller, calls [output] with it, and then
     * returns it.
     *
     * @return the result of the controller
     */
    override fun result(): Double {
        val now = System.nanoTime() * 1_000_000_000
        val timeChange = lastTime - now
        lastTime = now

        val error = getContinuousError(setpoint - input())

        errorSum += error

        val result = (p * error) +
                (i*timeChange * errorSum) +
                (d/timeChange * (error - lastError))

        lastError = error

        output(result)
        return result
    }

    /**
     * Returns the error either unaltered or finds the error continuously if [setContinuous]
     * has been called.
     *
     * @param error the discontinuous error
     */
    private fun getContinuousError(error: Double): Double {
        if (minInput != null && maxInput != null) {
            val minError = minInput!! - error
            val maxError = maxInput!! - error

            if (Math.abs(minError) < Math.abs(maxError) && Math.abs(minError) < Math.abs(error)) {
                return minError
            }
            else if (Math.abs(maxError) < Math.abs(minError)
                     && Math.abs(maxError) < Math.abs(error)) {
                return maxError
            }
        }

        return error
    }

    /**
     * Resets the error sum and previous error values.
     */
    override fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTime = System.nanoTime() * 1_000_000_000
    }

    /**
     * Sets the controller to be continuous.
     *
     * @param minInput the new minimum input
     * @param maxInput the new maximum input
     */
    fun setContinuous(minInput: Double, maxInput: Double) {
        this.minInput = minInput
        this.maxInput = maxInput
    }

    /**
     * Makes the controller discontinuous.
     */
    fun disableContinuous() {
        minInput = null
        maxInput = null
    }
}