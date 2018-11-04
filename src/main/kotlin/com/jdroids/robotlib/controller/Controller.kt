package com.jdroids.robotlib.controller

/**
 * The [Controller] interface represents a basic control loop.
 */
interface Controller {
    /**
     * This is a lambda that should return the current state of the thing being
     * controlled.
     */
    val input: () -> Double

    /**
     * This is a lambda that should set the state of the thing being controlled.
     */
    val output: (Double) -> Unit

    /**
     * This is the target for the [Controller].
     */
    val setpoint: Double

    /**
     * This calculates the current result of the [Controller].
     *
     * @return the result of the [Controller].
     */
    fun result(): Double

    /**
     * This should reset of all the things that can possibly be reset within the
     * controller.
     */
    fun reset()
}