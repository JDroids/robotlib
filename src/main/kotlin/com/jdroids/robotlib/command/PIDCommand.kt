package com.jdroids.robotlib.command

import com.jdroids.robotlib.pid.SimplePIDController
import com.jdroids.robotlib.util.Boundary
import com.qualcomm.robotcore.hardware.PIDCoefficients

/**
 * This class defines a [Command] which interacts heavily with a PID loop.
 *
 * It provides some convenience methods to run an internal [SimplePIDController]. It will also
 * start and stop said [SimplePIDController] when the [PIDCommand] is first initialized and
 * ended/interrupted.
 */
abstract class PIDCommand
/**
 * Instantiates a [PIDCommand] that will use the given p, i, d, v, and a values.
 *
 * @param p the proportional value
 * @param i the integral value
 * @param d the derivative value
 * @param v the feedforward velocity value
 * @param a the feedforward acceleration value
 */(input: () -> Double, output: (Double) -> Unit, pidCoefficients: PIDCoefficients, v: Double = 0.0, a: Double = 0.0) : Command() {
    /**
     * The internal [SimplePIDController].
     */
    private val controller = SimplePIDController(input, output, pidCoefficients, v, a)

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
        controller.setpoint = setpoint
    }

    /**
     * Returns the setpoint
     *
     * @return the setpoint
     */
    protected fun getSetpoint(): Double = controller.setpoint

    /**
     * Returns the current position.
     *
     * @return the current position
     */
    protected fun getPosition(): Double = controller.input()
    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param boundary the input boundary
     */
    fun setInputRange(boundary: Boundary) {
        controller.inputBoundary = boundary
    }
}