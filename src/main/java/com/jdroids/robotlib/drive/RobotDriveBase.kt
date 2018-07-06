package com.jdroids.robotlib.drive

abstract class RobotDriveBase(private val squareInputs: Boolean) {
    /**
     * Returns 0.0 if the input is below a certain value.
     */
    protected fun applyDeadband(value: Double, deadband: Double=0.02) = if (value < deadband) 0.0
        else value

    /**
     * Limit a given value to something that can be applied to a motor
     */
    protected fun limit(value: Double) = if(Math.abs(value) > 1.0) Math.signum(value) * 1.0
        else value

    /**
     * Given an array of motor speeds, if the highest speed is greater than 1.0, change all the
     * values to be divided by the highest speed.
     *
     * If an input array looks like [20.0, 5.0, 10.0, 15.0], the output would be
     * [1.0, 0.25, 0.5, 0.75].
     */
    @Synchronized
    fun normalize(motorSpeeds: DoubleArray) {
        val largestSpeed =
                DoubleArray(motorSpeeds.size, {i -> Math.abs(motorSpeeds[i])}).max()

        if (largestSpeed!! > 1.0) {
            for (i in motorSpeeds.indices) {
                motorSpeeds[i] = motorSpeeds[i]/largestSpeed
            }
        }
    }

    protected fun scaleInputs(value: Double): Double =
            if (squareInputs) Math.pow(value, 2.0) else value

    protected fun correctInputs(value: Double): Double = applyDeadband(limit(value))

}