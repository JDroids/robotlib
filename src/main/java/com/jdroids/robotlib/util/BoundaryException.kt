package com.jdroids.robotlib.util

/**
 * This exception represents an error in which a lower limit was set as higher than an upper limit.
 *
 * @param message the message to attach to the exception
 */
class BoundaryException(message: String) : RuntimeException(message) {
    companion object {
        /**
         * Make sure that the given value is between the upper and lower bounds, and throw an exception if
         * they are not.
         *
         * @param value The value to check.
         * @param lower The minimum acceptable value.
         * @param upper The maximum acceptable value.
         */
        fun assertWithinBounds(value: Double, lower: Double, upper: Double) {
            if (value < lower || value > upper) {
                throw BoundaryException("Value must be between $lower and $upper, $value given")
            }
        }

        /**
         * Returns the message for a boundary exception. Used to keep the message consistent across all
         * boundary exceptions.
         *
         * @param value The given value
         * @param lower The lower limit
         * @param upper The upper limit
         * @return the message for a boundary exception
         */
        fun getMessage(value: Double, lower: Double, upper: Double): String {
            return "Value must be between $lower and $upper, $value given"
        }
    }
}