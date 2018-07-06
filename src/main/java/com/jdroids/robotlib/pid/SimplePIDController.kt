package com.jdroids.robotlib.pid

import com.jdroids.robotlib.util.EnhancedElapsedTime
import com.jdroids.robotlib.filters.LinearDigitalFilter
import com.jdroids.robotlib.util.BoundaryException
import java.util.concurrent.locks.ReentrantLock
import java.lang.Compiler.disable
import java.lang.Compiler.enable




/**
 * Class implements a PIDVA Control Loop.
 *
 * Creates a seperate thread which reads the given [PIDSource] and takes care of the integral
 * calculations, as well as writing the given [PIDOutput].
 */
open class SimplePIDController : PIDInterface, PIDOutput {
    //Coefficient for proportional control
    private var pCoefficient = 0.0

    //Coefficient for integral control
    private var iCoefficient = 0.0

    //Coefficient for derivative control
    private var dCoefficient = 0.0

    //Coefficient for feedforward velocity control
    private var vCoefficient = 0.0

    //Coefficient for feedforward acceleration control
    private var aCoefficient = 0.0

    //Maximum output
    private var maxOutput = 1.0

    //Minimum output
    private var minOutput = -1.0

    //Minimum input - limit setpoint to this
    private var minInput = 0.0

    //Maximum input - limit setpoint to this
    private var maxInput = 0.0

    //Input range - difference between maximum and minimum
    private var inputRange = 0.0

    //The maximum velocity of the robot
    private var maxVelocity = 0.0

    //The maximum acceleration of the robot
    private var maxAcceleration = 0.0

    //Do the endpoints wrap around? (eg. absolute encoder, turning robot, etc.)
    private var continuous = false

    //Is the controller enabled?
    private var enabled = true

    //Used to calculate velocity
    private var prevError = 0.0

    //Used to calculate acceleration
    private var prevVel = 0.0

    //The tolerance object used to check if on target
    private var tolerance: Tolerance

    private var currentSetpoint = 0.0
    private var prevSetpoint = 0.0
    private var currentVelocitySetpoint = 0.0
    private var currentAccelerationSetpoint = 0.0
    private var currentError = 0.0
    private var result = 0.0

    private var origSource: PIDSource

    protected var thisMutex = ReentrantLock()

    // Ensures when disable() is called, pidWrite() won't run if calculate()
    // is already running at that time.
    protected var pidWriteMutex = ReentrantLock()

    protected var pidInput: PIDSource
    protected var pidOutput: PIDOutput
    private val timer = EnhancedElapsedTime()
    private val filter: LinearDigitalFilter

    // The sum of the errors for use in the integral calc
    private var totalError = 0.0

    private var lastTime = timer.seconds()

    /**
     * Tolerance is the type of tolerance used to specify if the PID controller is on target.
     *
     * The various implementations of this interface such as PercentageTolerance and
     * AbsoluteTolerance specify types of tolerance specifications to use.
     */
    interface Tolerance {
        fun onTarget(): Boolean
    }

    /**
     * Used internally for when Tolerance hasn't been set.
     */
    class NullTolerance : Tolerance {
        override fun onTarget(): Boolean {
            throw IllegalStateException("No tolerance value set when calling onTarget().")
        }
    }

    inner class PercentageTolerance(private val percentage: Double) : Tolerance {
        override fun onTarget(): Boolean = Math.abs(getError()) < percentage/100 * inputRange
    }

    inner class AbsoluteTolerance(private val value: Double) : Tolerance {
        override fun onTarget(): Boolean = Math.abs(getError()) < value
    }

    /**
     * Allocate a PID object with the given P, I, D, V, and A coefficients.
     *
     * @param p the proportional coefficient
     * @param i the integral coefficient (optional, defaults to 0)
     * @param d the derivative coefficient (optional, defaults to 0)
     * @param v the feedforward velocity coefficient (optional, defaults to 0)
     * @param a the feedforward acceleration coefficient (optional, defaults to 0)
     */
    constructor(p: Double, i: Double=0.0, d: Double=0.0, v: Double=0.0, a: Double=0.0,
                source: PIDSource, output: PIDOutput) {
        timer.reset()
        setPID(p, i, d, v, a)

        //Save original source
        origSource = source

        filter = LinearDigitalFilter.movingAverage(origSource, 1)
        pidInput = filter

        pidOutput = output

        tolerance = NullTolerance()
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output. This should only be
     * called by the PIDTask and is created during initialization.
     */
    protected fun calculate() {
        val isEnabled: Boolean

        thisMutex.lock()
        try {
            isEnabled = enabled
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

            //Storage for function input
            val pidSourceType: PIDSource.Type
            val pTemp: Double
            val iTemp: Double
            val dTemp: Double
            val feedForward = calculateFeedForward()
            val minimumOutput: Double
            val maximumOutput: Double

            //Storage for function input-outputs
            val previousError: Double
            val error: Double
            var totError: Double

            thisMutex.lock()
            try {
                input = pidInput.pidGet()

                pidSourceType = pidInput.getPIDSourceType()

                pTemp = pCoefficient
                iTemp = iCoefficient
                dTemp = dCoefficient
                minimumOutput = minOutput
                maximumOutput = maxOutput

                previousError = prevError
                error = getContinuousError(currentSetpoint - input)
                totError = totalError
            }
            finally {
                thisMutex.unlock()
            }
            // Storage for function outputs
            var tempResult: Double
            if (pidSourceType == PIDSource.Type.RATE) {
                if (pTemp != 0.0) {
                    totError = clamp(totError + (error/deltaTime), minimumOutput / pTemp,
                            maximumOutput / pTemp)
                }
                tempResult = pTemp * totError + dTemp * (error/deltaTime) + feedForward
            }
            else {
                if (iTemp != 0.0) {
                    totError = clamp(totError + (error/deltaTime), minOutput / iTemp,
                            maxOutput / iTemp)
                }
                tempResult = pTemp*error + iTemp*totError + dTemp*((error-prevError)/deltaTime) +
                        feedForward
            }

            tempResult = clamp(tempResult, minimumOutput, maximumOutput)

            //Ensures enabled check and pidWrite call occur automatically
            pidWriteMutex.lock()
            try {
                thisMutex.lock()
                try {
                    if (enabled) {
                        //Don't block other SimplePIDController operations on pidWrite()
                        thisMutex.unlock()

                        pidOutput.pidWrite(tempResult)
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
                prevError = error
                currentError = error
                totalError = totError
                result = tempResult
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
     * Calculate the feedforward terms (velocity and acceleration).
     *
     * If a different feed forward calculation is desired, the user can override this function and
     * provide his or her own. This function  does no synchronization because the SimplePIDController
     * class only calls it in synchronized code, so be careful if calling it oneself.
     *
     * If a velocity PID controller is being used, the V term should be set to 1 over the maximum
     * setpoint for the output. If a position PID controller is being used, the V term should be set
     * to 1 over the maximum speed for the output measured in setpoint units per this controller's
     * update period (see the default period in this class's constructor).
     */
    protected fun calculateFeedForward(): Double {
        val timeDelta = timer.seconds() - lastTime

        val velocity = when(pidInput.getPIDSourceType()) {
            PIDSource.Type.RATE -> getSetpoint()
            PIDSource.Type.DISPLACEMENT -> (currentError - prevError) / timeDelta
        }

        val acceleration = (velocity - prevVel) / timeDelta

        prevVel = velocity

        return vCoefficient * velocity + aCoefficient * acceleration
    }

    override fun setPID(p: Double, i: Double, d: Double, v: Double, a: Double) {
        thisMutex.lock()
        try {
            pCoefficient = p
            iCoefficient = i
            dCoefficient = d
            vCoefficient = v
            aCoefficient = a
        }
        finally {
            thisMutex.unlock()
        }
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        thisMutex.lock()

        try {
            pCoefficient = p
            iCoefficient = i
            dCoefficient = d
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the proportional coefficient of the PID controller gain.
     *
     * @param p Proportional coefficient
     */
    override fun setP(p: Double) {
        thisMutex.lock()
        try {
            pCoefficient = p
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the integral coefficient of the PID controller gain.
     *
     * @param i integral coefficient
     */
    override fun setI(i: Double) {
        thisMutex.lock()
        try {
            iCoefficient = i
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the derivative coefficient of the PID controller gain.
     *
     * @param d derivative coefficient
     */
    override fun setD(d: Double) {
        thisMutex.lock()
        try {
            dCoefficient = d
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the feed forward velocity coefficient of the PID controller gain.
     *
     * @param v feed forward velocity coefficient
     */
    override fun setV(v: Double) {
        thisMutex.lock()
        try {
            vCoefficient = v
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the feed forward acceleration coefficient of the PID controller gain.
     *
     * @param a feed acceleration velocity coefficient
     */
    override fun setA(a: Double) {
        thisMutex.lock()
        try {
            aCoefficient = a
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Get the Proportional coefficient
     *
     * @return proportional coefficient
     */
    override fun getP(): Double = pCoefficient

    /**
     * Get the Integral coefficient
     *
     * @return integral coefficient
     */
    override fun getI(): Double = iCoefficient

    /**
     * Get the Derivative coefficient
     *
     * @return derivative coefficient
     */
    override fun getD(): Double = dCoefficient

    /**
     * Get the Feedforward Velocity coefficient
     *
     * @return feedforward velocity coefficient
     */
    override fun getV(): Double = vCoefficient

    /**
     * Get the Feedforward Acceleration coefficient
     *
     * @return feedforward acceleration coefficient
     */
    override fun getA(): Double = aCoefficient

    /**
     * Return the current PID result. This is always centered on zero and constrained between the
     * min and max outputs.
     *
     * @return the latest calculated output
     */
    fun get(): Double {
        thisMutex.lock()
        try {
            return result
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the PID controller to consider the input to be continuous. Rather than using the max and
     * min input as constraints, it considers them to be the same point and automatically calculates
     * the shortest route to the setpoint.
     *
     * @param continuous Set to true it turns on continuous, set to false it turns off continuous
     *      (defaults to true)
     */
    fun setContinuous(continuous: Boolean=true) {
        if (continuous && inputRange <= 0) {
            throw IllegalStateException("No input range set when calling setContinuous().")
        }
        thisMutex.lock()
        try {
            this.continuous = continuous
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Sets the maximum and minimum values expected from the input and setpoint.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the input
     */
    fun setInputRange(minimumInput: Double, maximumInput: Double) {
        thisMutex.lock()
        try {
            if (maximumInput > minimumInput) {
                throw BoundaryException("Lower bound is greater than larger bound")
            }
            minInput = minimumInput
            maxInput = maximumInput
            inputRange = maximumInput - minimumInput
        }
        finally {
            thisMutex.unlock()
        }

        setSetpoint(currentSetpoint)
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum value to write to the output
     * @param maximumOutput the maximum value to write to the output
     */
    fun setOutputRange(minimumOutput: Double, maximumOutput: Double) {
        thisMutex.lock()
        try {
            if (minimumOutput > maximumOutput) {
                throw BoundaryException("Lower bound is greater than upper bound")
            }
            minOutput = minimumOutput
            maxOutput = maximumOutput
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the setpoint for the SimplePIDController.
     *
     * @param setpoint the desired setpoint
     */
    override fun setSetpoint(setpoint: Double) {
        thisMutex.lock()
        try {
            if (maxInput > minInput) {
                currentSetpoint = when {
                    setpoint > maxInput -> maxInput
                    setpoint < minInput -> minInput
                    else -> setpoint
                }
            }
            else {
                currentSetpoint = setpoint
            }
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Returns the current setpoint of the PID controller.
     *
     * @return the current setpoint
     */
    override fun getSetpoint(): Double {
        thisMutex.lock()
        try {
            return currentSetpoint
        } finally {
            thisMutex.unlock()
        }
    }

    /**
     * Returns the change in setpoint over time of the SimplePIDController.
     *
     * @return the change in setpoint over time
     */
    fun getDeltaSetpoint(): Double {
        thisMutex.lock()
        try {
            return (currentSetpoint - prevSetpoint) / timer.seconds()
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Returns the current difference of the input from the setpoint.
     *
     * @return the current error
     */
    override fun getError(): Double {
        thisMutex.lock()
        try {
            return getContinuousError(getSetpoint() - pidInput.pidGet())
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Returns the type of input the PID controller is using.
     *
     * @return the PID controller input type
     */
    internal fun setPIDSourceType(type: PIDSource.Type) {
        pidInput.setPIDSourceType(type)
    }

    internal fun getPIDSourceType(): PIDSource.Type = pidInput.getPIDSourceType()


    /**
     * Set the absolute error which is considered tolerable for use with OnTarget.
     *
     * @param absvalue absolute error which is tolerable in the units of the input object
     */
    fun setAbsoluteTolerance(absvalue: Double) {
        thisMutex.lock()
        try {
            tolerance= AbsoluteTolerance(absvalue)
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Set the percentage error which is considered tolerable for use with OnTarget. (Input of 15.0 =
     * 15 percent)
     *
     * @param percentage percent error which is tolerable
     */
    fun setPercentTolerance(percentage: Double) {
        thisMutex.lock()
        try {
            tolerance = PercentageTolerance(percentage)
        }
        finally {
            thisMutex.unlock()
        }
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
            return tolerance.onTarget()
        } finally {
            thisMutex.unlock()
        }
    }

    /**
     * Reset the previous error, the integral term, and disable the controller.
     */
    override fun reset() {
        thisMutex.lock()
        try {
            prevError = 0.0
            totalError = 0.0
            result = 0.0
        }
        finally {
            thisMutex.unlock()
        }
    }

    /**
     * Passes the output directly to setSetpoint().
     *
     * PIDControllers can be nested by passing a SimplePIDController as another SimplePIDController's output.
     * In that case, the output of the parent controller becomes the input (i.e., the reference) of
     * the child.
     *
     * It is the caller's responsibility to put the data into a valid form for setSetpoint().
     */
    override fun pidWrite(output: Double) {
        setSetpoint(output)
    }


    /**
     * Wraps error around for continuous inputs. The original error is returned if continuous mode is
     * disabled. This is an unsynchronized function.
     *
     * @param error The current error of the PID controller.
     * @return Error for continuous inputs.
     */
    protected fun getContinuousError(error: Double): Double {
        var error = error
        if (continuous && inputRange > 0) {
            error %= inputRange
            if (Math.abs(error) > inputRange / 2) {
                return if (error > 0) {
                    error - inputRange
                }
                else {
                    error + inputRange
                }
            }
        }
        return error
    }

    /**
     * Begin running the SimplePIDController.
     */
    fun enable() {
        thisMutex.lock()
        try {
            enabled = true
        } finally {
            thisMutex.unlock()
        }
    }
    
    /**
     * Stop running the SimplePIDController, this sets the output to zero before stopping.
     */
    fun disable() {
        timer.pause()
        pidWriteMutex.lock()
        try {
            thisMutex.lock()
            try {
                enabled = false
            }
            finally {
                thisMutex.unlock()
            }
            pidOutput.pidWrite(0.0)
        }
        finally {
            pidWriteMutex.unlock()
        }
    }

    /**
     * Set the enabled state of the SimplePIDController.
     */
    fun setEnabled(enable: Boolean) {
        if (enable) {
            enable()
        } else {
            disable()
        }
    }

    /**
     * Return true if SimplePIDController is enabled.
     */
    fun isEnabled(): Boolean {
        thisMutex.lock()
        try {
            return enabled
        } finally {
            thisMutex.unlock()
        }
    }

    companion object {
        fun clamp(value: Double, low: Double, high: Double): Double =
                Math.max(low, Math.min(value, high))
    }
}