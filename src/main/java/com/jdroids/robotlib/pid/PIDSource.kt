package com.jdroids.robotlib.pid

/**
 * This interface allows for [PIDController] to automatically read from this object.
 */
interface PIDSource {
    /**
     * Set which parameter of the device you are using as a process control variable.
     */
    fun setPIDSourceType(type: Type)

    /**
     * Get which parameter of the device you are using as a process control variable.
     *
     * @return the currently selected PID source parameter
     */
    fun getPIDSourceType(): Type

    /**
     * Get the result to use in PIDController.
     *
     * @return the result to use in PIDController
     */
    fun pidGet(): Double

    /**
     * A description for the type of output values to provide to a [PIDController].
     */
    enum class Type {
        DISPLACEMENT,
        RATE
    }
}