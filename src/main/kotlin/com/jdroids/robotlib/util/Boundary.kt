package com.jdroids.robotlib.util

/**
 * This class is supposed to represent a boundary between two numbers.
 *
 * @param lower The minimum acceptable value.
 * @param upper The maximum acceptable value.
 */
data class Boundary(val lower: Double, val upper: Double) {
    init {
        if (lower > upper) {
            throw BoundaryException("Given a lower value greater than the upper value, given " +
                    "$this (lower and upper)")
        }
    }

    override fun toString(): String = "$lower and $upper"

    class BoundaryException(message: String="") : RuntimeException(message) {
        companion object {
            /**
             * Make sure that the given value is between the upper and lower bounds, and throw an
             * exception if they are not.
             *
             * @param value The value to check.
             * @param boundary the [Boundary] to get the upper and lower bounds from
             */
            fun assertWithinBounds(value: Double, boundary: Boundary) {
                if (value < boundary.lower || value > boundary.upper) {
                    throw BoundaryException("Value must be between $boundary, $value given")
                }
            }
        }
    }
}