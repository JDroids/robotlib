package com.jdroids.robotlib.command

import com.jdroids.robotlib.pid.PIDInterface
import com.jdroids.robotlib.pid.SimplePIDController
import com.jdroids.robotlib.util.Boundary
import com.qualcomm.robotcore.hardware.PIDCoefficients

/**
 * This class is designed to handle the case where there is a [Subsystem] which uses a single
 * [SimplePIDController] almost constantly (for instance, an elevator which attempts to stay at a
 * constant height).
 *
 * It provides some convinience methods to run an internal [SimplePIDController]. It also allows
 * access to the internal [SimplePIDController] in order to give total control to the programmer.
 */
abstract class PIDSubsystem
/**
 * Instantiates a [PIDSubsystem] that will use the given p, i, d, v, and a values.
 *
 * @param p the proportional value
 * @param i the integral value
 * @param d the derivative value
 * @param v the feedforward velocity value
 * @param a the feedforward acceleration value
 */(input: () -> Double, output: (Double) -> Unit, pidCoefficients: PIDCoefficients,
    v: Double = 0.0, a: Double = 0.0) : Subsystem() {
    /**
     * The internal [SimplePIDController]
     */
    protected val controller: SimplePIDController = SimplePIDController(input, output,
            pidCoefficients, v, a)

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
        controller.setpoint = setpoint
    }

    /**
     * Returns the setpoint.
     *
     * @return the setpoint
     */
    fun getSetpoint(): Double = controller.setpoint

    /**
     * Returns the current position.
     *
     * @return the current position
     */
    fun getPosition(): Double = controller.input()

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param boundary the input boundary
     */
    fun setInputRange(boundary: Boundary) {
        controller.inputBoundary = boundary
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param boundary the input boundary
     */
    fun setOutputRange(boundary: Boundary) {
        controller.outputBoundary = boundary
    }

    /**
     * Set the absolute error which is considered tolerable for use with OnTarget. The value is in
     * the same range as the PIDInput values.
     *
     * @param t the absolute tolerance
     */
    fun setAbsoluteTolerance(t: Double) {
        controller.tolerance = PIDInterface.Tolerance.absoluteTolerance(t)
    }

    /**
     * Set the percentage error which is considered tolerable for use with OnTarget. (Value of 15.0
     * == 15 percent).
     *
     * @param p the percent tolerance
     */
    fun setPercentTolerance(p: Double) {
        controller.tolerance = PIDInterface.Tolerance.percentageTolerance(p)
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
     * Enables the internal [PIDController].
     */
    fun enable() {
        controller.enabled = true
    }

    /**
     * Disables the internal [PIDController].
     */
    fun disable() {
        controller.enabled = false
    }
}