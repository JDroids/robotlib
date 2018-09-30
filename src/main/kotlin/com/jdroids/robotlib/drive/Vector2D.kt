package com.jdroids.robotlib.drive

/**
 * A 2D Vector struct that supports basic vector operations.
 */
class Vector2D(var x: Double, var y: Double) {
    /**
     * Rotate a vector in 2D Cartesian space
     *
     * @param angle the angle in radians by which to order the vector counterclockwise
     */
    fun rotate(angle: Double) {
        val cosAngle = Math.cos(angle)
        val sinAngle = Math.sin(angle)

        val outputX = x * cosAngle - y * sinAngle
        val outputY = x * sinAngle + y * cosAngle

        x = outputX
        y = outputY
    }

    /**
     * Returns dot product of this vector with the provided argument
     *
     * @param vec vector with which to calculate the dot product
     */
    fun dot(vec: Vector2D): Double = x * vec.x + y * vec.y

    /**
     * Returns the magnitude of the vector
     */
    fun magnitude(): Double = Math.sqrt(x * x + y * y)

    /**
     * Returns scalar projection of this vector onto argument.
     *
     * @param vec the vector onto which to project this vector
     */
    fun scalarProject(vec: Vector2D): Double = dot(vec) / vec.magnitude()
}
