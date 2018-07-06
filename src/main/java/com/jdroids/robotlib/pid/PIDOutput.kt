package com.jdroids.robotlib.pid

/**
 * This interface allows PIDController to write it's results to its output.
 */
interface PIDOutput {
    /**
     * Set the output to the value calculated by PIDController.
     *
     * @param output the value calculated by PIDController
     */
    fun pidWrite(output: Double)
}