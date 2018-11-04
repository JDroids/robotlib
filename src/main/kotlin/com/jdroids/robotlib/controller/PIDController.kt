package com.jdroids.robotlib.controller

/**
 * The [PIDController] represents a simple
 * [pid controller](https://en.wikipedia.org/wiki/PID_controller).
 */
interface PIDController : Controller {
    /**
     * The p coefficient of the pid controller.
     */
    val p: Double

    /**
     * The i coefficient of the pid controller.
     */
    val i: Double

    /**
     * The d coefficient of the pid controller.
     */
    val d: Double
}