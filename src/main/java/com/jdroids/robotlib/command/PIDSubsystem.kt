package com.jdroids.robotlib.command

import com.jdroids.robotlib.pid.SimplePIDController
import com.jdroids.robotlib.pid.PIDOutput
import com.jdroids.robotlib.pid.PIDSource
import java.lang.Compiler.disable
import java.lang.Compiler.enable



/**
 * This class is designed to handle the case where there is a [Subsystem] which uses a single
 * [SimplePIDController] almost constantly (for instance, an elevator which attempts to stay at a
 * constant height).
 *
 * It provides some convinience methods to run an internal [SimplePIDController]. It also allows
 * access to the internal [SimplePIDController] in order to give total control to the programmer.
 */
abstract class PIDSubsystem : Subsystem {
    /**
     * The internal [SimplePIDController]
     */
    protected var controller: SimplePIDController
    /**
     * An output which calls [PIDCommand.usePIDOutput]
     */
    private val output = Output()
    /**
     * A source which calls [PIDCommand.returnPIDInput]
     */
    private val source = Source()

    /**
     * Instantiates a [PIDSubsystem] that will use the given p, i, d, v, and a values.
     *
     * @param p the proportional value
     * @param i the integral value
     * @param d the derivative value
     * @param v the feedforward velocity value
     * @param a the feedforward acceleration value
     */
    constructor(name: String, p: Double, i: Double, d: Double, v: Double, a: Double) {
        controller = SimplePIDController(p, i, d, v, a, source, output)
    }

    /**
     * Returns the [SimplePIDController] used by this [PIDSubsystem].
     */
    fun getPIDController(): SimplePIDController = controller

    /**
     * Adds the given value to the setpoint. If [PIDSubsystem.setInputRange] was used, then the
     * bounds will still be honored by this method.
     *
     * @param deltaSetpoint the change in the setpoint
     */
    fun setSetpointRelative(deltaSetpoint: Double) {
        setSetpoint(getPosition() + deltaSetpoint)
    }

    /**
     * Sets the setpoint to the given value. IF [PIDSubsystem.setInputRange] was called, then the
     * given setpoint will be trimmed to fit within the range.
     *
     * @param setpoint the new setpoint
     */
    fun setSetpoint(setpoint: Double) {
        controller.setSetpoint(setpoint)
    }

    /**
     * Returns the setpoint.
     *
     * @return the setpoint
     */
    fun getSetpoint(): Double = controller.getSetpoint()

    /**
     * Returns the current position.
     *
     * @return the current position
     */
    fun getPosition(): Double = returnPIDInput()

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the output
     */
    fun setInputRange(minimumInput: Double, maximumInput: Double) {
        controller.setInputRange(minimumInput, maximumInput)
    }

    /**
     * Sets the maximum and minimum values to write.
     *
     * @param minimumOutput the minimum value to write to the output
     * @param maximumOutput the maximum value to write to the output
     */
    fun setOutputRange(minimumOutput: Double, maximumOutput: Double) {
        controller.setOutputRange(minimumOutput, maximumOutput)
    }

    /**
     * Set the absolute error which is considered tolerable for use with OnTarget. The value is in
     * the same range as the PIDInput values.
     *
     * @param t the absolute tolerance
     */
    fun setAbsoluteTolerance(t: Double) {
        controller.setAbsoluteTolerance(t)
    }

    /**
     * Set the percentage error which is considered tolerable for use with OnTarget. (Value of 15.0
     * == 15 percent).
     *
     * @param p the percent tolerance
     */
    fun setPercentTolerance(p: Double) {
        controller.setPercentTolerance(p)
    }

    /**
     * Return true if the error is within the percentage of the total input range, determined by
     * setTolerance. This assumes that the maximum and minimum input were set using setInput.
     *
     * @return true if the error is less than the tolerance
     */
    fun onTarget(): Boolean {
        return controller.onTarget()
    }

    /**
     * Returns the input for the pid loop.
     *
     *
     * It returns the input for the pid loop, so if this Subsystem was based off of a gyro, then
     * it should return the angle of the gyro.
     *
     *
     * All subclasses of [PIDSubsystem] must override this method.
     *
     * @return the value the pid loop should use as input
     */
    protected abstract fun returnPIDInput(): Double

    /**
     * Uses the value that the pid loop calculated. The calculated value is the "output" parameter.
     * This method is a good time to set motor values, maybe something along the lines of
     * `driveline.tankDrive(output, -output)`.
     *
     *
     * All subclasses of [PIDSubsystem] must override this method.
     *
     * @param output the value the pid loop calculated
     */
    protected abstract fun usePIDOutput(output: Double)

    /**
     * Enables the internal [PIDController].
     */
    fun enable() {
        controller.enable()
    }

    /**
     * Disables the internal [PIDController].
     */
    fun disable() {
        controller.disable()
    }



    private inner class Output : PIDOutput {
        override fun pidWrite(output: Double) {
            usePIDOutput(output)
        }
    }

    private inner class Source : PIDSource {
        override fun setPIDSourceType(type: PIDSource.Type) {}

        override fun getPIDSourceType(): PIDSource.Type {
            return PIDSource.Type.DISPLACEMENT
        }

        override fun pidGet(): Double {
            return returnPIDInput()
        }
    }
}