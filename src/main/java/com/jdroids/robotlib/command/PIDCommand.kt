package com.jdroids.robotlib.command

import com.jdroids.robotlib.pid.SimplePIDController
import com.jdroids.robotlib.pid.PIDOutput
import com.jdroids.robotlib.pid.PIDSource

/**
 * This class defines a [Command] which interacts heavily with a PID loop.
 *
 * It provides some convenience methods to run an internal [SimplePIDController]. It will also
 * start and stop said [SimplePIDController] when the [PIDCommand] is first initialized and
 * ended/interrupted.
 */
abstract class PIDCommand : Command {
    /**
     * The internal [SimplePIDController].
     */
    private val controller: SimplePIDController
    /**
     * An output which calls [usePIDOutput].
     */
    private val output = Output()
    /**
     * A source which calls [returnPIDInput].
     */
    private val source = Source()

    /**
     * Instantiates a [PIDCommand] that will use the given p, i, d, v, and a values.
     *
     * @param p the proportional value
     * @param i the integral value
     * @param d the derivative value
     * @param v the feedforward velocity value
     * @param a the feedforward acceleration value
     */
    constructor(p: Double, i: Double, d: Double, v: Double=0.0, a: Double=0.0) {
        controller = SimplePIDController(source, output, p, i, d, v, a)
    }

    /**
     * Returns the [SimplePIDController] used by this [PIDCommand]. Use this if you want to fine-tune the PID
     * loop.
     *
     * @return the [SimplePIDController] used by this [PIDCommand]
     */
    protected fun getPIDController(): SimplePIDController = controller

    /**
     * Adds the given value to the setpoint. If {@link PIDCommand#setInputRange(double, double)
     * setInputRange(...)} was used, then the bounds will still be honored by this method.
     *
     * @param deltaSetpoint the change in the setpoint
     */
    fun setSetpointRelative(deltaSetpoint: Double) {
        setSetpoint(getSetpoint() + deltaSetpoint)
    }

    /**
     * Sets the setpoint to the given value. If [setInputRange][PIDCommand.setInputRange] was
     * called, then the given setpoint will be trimmed to fit within the
     * range.
     *
     * @param setpoint the new setpoint
     */
    protected fun setSetpoint(setpoint: Double) {
        controller.setSetpoint(setpoint)
    }

    /**
     * Returns the setpoint
     *
     * @return the setpoint
     */
    protected fun getSetpoint(): Double = controller.getSetpoint()

    /**
     * Returns the current position.
     *
     * @return the current position
     */
    protected fun getPosition(): Double = returnPIDInput()

    /**
     * Sets the maximum and minimum values expected from the input and setpoint.
     *
     * @param minimumInput the minimum value expected from the input and setpoint
     * @param maximumInput the maximum value expected from the input and setpoint
     */
    protected fun setInputRange(minimumInput: Double, maximumInput: Double) {
        controller.setInputRange(minimumInput, maximumInput)
    }

    /**
     * Returns the input for the pid loop.
     *
     *
     * It returns the input for the pid loop, so if this command was based off of a gyro, then it
     * should return the angle of the gyro.
     *
     *
     * All subclasses of [PIDCommand] must override this method.
     *
     *
     * This method will be called in a different thread then the [Scheduler] thread.
     *
     * @return the value the pid loop should use as input
     */
    protected abstract fun returnPIDInput(): Double

    /**
     * Uses the value that the pid loop calculated. The calculated value is the "output" parameter.
     * This method is a good time to set motor values, maybe something along the lines of
     * `driveline.tankDrive(output, -output)`
     *
     *
     * All subclasses of [PIDCommand] must override this method.
     *
     *
     * This method will be called in a different thread then the [Scheduler] thread.
     *
     * @param output the value the pid loop calculated
     */
    protected abstract fun usePIDOutput(output: Double)

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