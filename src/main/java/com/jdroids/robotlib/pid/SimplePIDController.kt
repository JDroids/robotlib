package com.jdroids.robotlib.pid

import com.jdroids.robotlib.util.Boundary
import com.jdroids.robotlib.util.EnhancedElapsedTime
import com.jdroids.robotlib.util.MathUtil
import com.qualcomm.robotcore.hardware.PIDCoefficients
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

/**
 * This class is meant to implement a fairly simple PID controller (with optional feedforward
 * elements). It takes a value from [input], does the necessary calculations to it to get
 * [result], and then passes [result] to the [output] lambda.
 */
open class SimplePIDController
/**
 * This creates a [SimplePIDController].
 *
 * @param input a lambda that is used to determine the current position of the system.
 * @param output This is a lambda that is supposed to apply [result] to the system.
 * @param pidCoefficients the wanted PID Coefficients
 * @param v the wanted feedforward velocity coefficient (defaults to 0)
 * @param a the wanted feedforward acceleration coefficient (defaults to 0)
 * @param setpoint setpoint the setpoint for the control loop
 */(override var input: (() -> Double), override var output: ((Double) -> Unit),
    override var pidCoefficients: PIDCoefficients, override var v: Double = 0.0,
    override var a: Double = 0.0) : PIDInterface {

    open var error: Double = 0.0
        protected set

    var inputBoundary: Boundary? = null

    var outputBoundary: Boundary? = null

    //Whether or not the outputs wrap around
    var continuous = false

    var tolerance = PIDInterface.Tolerance.nullTolerance()

    var enabled = true

    protected open var timer = EnhancedElapsedTime()

    protected open var lastTime = timer.seconds()

    protected open var previousError = 0.0

    protected open var previousVelocity = 0.0

    protected open var totalError = 0.0

    protected open val thisMutex = ReentrantLock()

    // Ensures when disable() is called, the input lambda won't be run if calculate()
    // is already running at that time.
    protected var pidWriteMutex = ReentrantLock()

    /**
     * The result that was calculated by the controller
     */
    override var result = 0.0

    /**
     * The setpoint of the control loop
     */
    override var setpoint: Double = 0.0

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     */
    fun calculate() {
        thisMutex.lock()
        val isEnabled = try {
            enabled
        }
        finally {
            thisMutex.unlock()
        }

        if (isEnabled) {
            if (timer.isPaused) {
                timer.start()
            }

            val currentTime = timer.seconds()
            val deltaTime = currentTime - lastTime

            lastTime = currentTime

            val input: Double
            val pidCoefficients: PIDCoefficients
            val feedForward = calculateFeedForward()
            val outputBoundary: Boundary?
            val error: Double

            thisMutex.lock()
            try {
                input = this.input()
                pidCoefficients = this.pidCoefficients
                outputBoundary = this.outputBoundary
                totalError = this.totalError
                error = getContinuousError(setpoint - input)
            }
            finally {
                thisMutex.unlock()
            }

            val minimumOutput = outputBoundary?.lower ?: Double.MIN_VALUE
            val maximumOutput = outputBoundary?.upper ?: Double.MAX_VALUE

            if (pidCoefficients.i != 0.0) {
                totalError = MathUtil.clamp(totalError + (error/deltaTime),
                        minimumOutput/pidCoefficients.p, maximumOutput/pidCoefficients.p)
            }
            val tempResult = MathUtil.clamp(pidCoefficients.p*error +
                    pidCoefficients.i*totalError +
                    pidCoefficients.d*(error-previousError)/deltaTime +
                    feedForward, minimumOutput, maximumOutput)

            pidWriteMutex.lock()
            try {
                thisMutex.lock()
                try {
                    if (isEnabled) {
                        thisMutex.unlock()

                        output(tempResult)
                    }
                }
                finally {
                    if (thisMutex.isHeldByCurrentThread) {
                        thisMutex.unlock()
                    }
                }
            }
            finally {
                pidWriteMutex.unlock()
            }

            thisMutex.lock()
            try {
                previousError = error
                this.error = error
                this.totalError = totalError
                this.result = tempResult
            }
            finally {
                thisMutex.unlock()
            }
        }
        else {
            timer.pause()
        }
    }

    /**
     * Reset the previous error, the integral term, and disable the controller.
     */
    override fun reset() {
        thisMutex.lock()
        try {
            previousError  = 0.0
            totalError = 0.0
            result = 0.0
            enabled = false
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Calculate the feedforward terms (velocity and acceleration).
     *
     * If a different feed forward calculation is desired, the user can override this function and
     * provide his or her own. This function  does no synchronization because the SimplePIDController
     * class only calls it in synchronized code, so be careful if calling it oneself.
     * The V term should be set to 1 over the maximum speed for the output measured in setpoint
     * units per secind.
     */
    protected fun calculateFeedForward(): Double {
        val timeDelta = timer.seconds() - lastTime

        val velocity = (error - previousError) / timeDelta
        val acceleration = (velocity - previousVelocity)  / timeDelta

        previousVelocity = velocity

        return v * velocity + a * acceleration
    }

    /**
     * Wraps error around for continuous inputs. The original error is returned if continuous mode is
     * disabled. This is an unsynchronized function.
     *
     * @param error The current error of the PID controller.
     * @return Error for continuous inputs.
     */
    protected fun getContinuousError(error: Double): Double {
        var errorMutable = error
        val inputRange = (inputBoundary?.upper ?: 0.0) - (inputBoundary?.lower ?: 0.0)
        if (continuous && inputRange > 0) {
            errorMutable %= inputRange
            if (Math.abs(errorMutable) > inputRange / 2) {
                return if (errorMutable > 0) {
                    errorMutable - inputRange
                }
                else {
                    errorMutable + inputRange
                }
            }
        }
        return errorMutable
    }

    /**
     * Return true if the error is within the percentage of the total input range, determined by
     * setTolerance. This assumes that the maximum and minimum input were set using setInput.
     *
     * @return true if the error is less than the tolerance
     */
    fun onTarget(): Boolean {
        thisMutex.lock()
        try {
            return tolerance.onTarget(error)
        } finally {
            thisMutex.unlock()
        }
    }
}