package com.jdroids.robotlib.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

/**
 * Allows [DcMotors][DcMotor] to be linked together
 */
class MotorGroup(vararg motors: DcMotor) {
    private val motors: Array<DcMotor> = Array(motors.size, {i -> motors[i]})

    /**
     * Set the speed for each of the motors in the MotorGroup to a given value
     *
     * @param power the power that is sent to each motor
     */
    fun setPower(power: Double) {
        for (motor in motors) {
            motor.power = power
        }
    }

    /**
     * Returns the power that is currently set to the DcMotors within the group
     */
    fun getPower(): Double {
        if (motors.size > 1) {
            return motors[0].power
        }
        return 0.0
    }

    /**
     * Brakes all the motors in the MotorGroup
     */
    fun brakeMotors() {
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
        setPower(0.0)
    }

    /**
     * Sets the Zero Power Behavior of each motor in the MotorGroup
     *
     * @param zeroPowerBehavior the wanted [ZeroPowerBehavior][DcMotor.ZeroPowerBehavior]
     */
    fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior) {
        for (motor in motors) {
            motor.zeroPowerBehavior = zeroPowerBehavior
        }
    }

    /**
     * Sets the RunMode of each motor in the MotorGroup
     *
     * @param runMode the wanted [RunMode][DcMotor.RunMode]
     */
    fun setRunMode(runMode: DcMotor.RunMode) {
        for (motor in motors) {
            motor.mode = runMode
        }
    }

    /**
     * Sets the direction of each motor in the MotorGroup
     *
     * @param direction the wanted direction
     */
    fun setDirection(direction: DcMotorSimple.Direction) {
        for (motor in motors) {
            motor.direction = direction
        }
    }
}