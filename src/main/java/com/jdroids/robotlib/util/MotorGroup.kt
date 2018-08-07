package com.jdroids.robotlib.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.configuration.MotorConfigurationType

/**
 * Allows DcMotors to be linked together
 */
class MotorGroup(vararg motors: DcMotor) : DcMotor {
    private val motors: Array<DcMotor> = Array(motors.size, {i -> motors[i]})

    /**
     * Set the speed for each of the motors in the MotorGroup to a given value
     *
     * @param power the power that is sent to each motor
     */
    override fun setPower(power: Double) {
        for (motor in motors) {
            motor.power = power
        }
    }

    /**
     * Returns the power that is currently set to the DcMotors within the group
     */
    override fun getPower(): Double = motors[0].power

    /**
     * Brakes all the motors in the MotorGroup
     */
    fun brakeMotors() {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        power = 0.0
    }

    /**
     * Sets the Zero Power Behavior of each motor in the MotorGroup
     *
     * @param zeroPowerBehavior the wanted [ZeroPowerBehavior][DcMotor.ZeroPowerBehavior]
     */
    override fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior) {
        for (motor in motors) {
            motor.zeroPowerBehavior = zeroPowerBehavior
        }
    }

    /**
     * Sets the RunMode of each motor in the MotorGroup
     *
     * @param runMode the wanted DcMotor.RunMode
     */
    override fun setMode(runMode: DcMotor.RunMode) {
        for (motor in motors) {
            motor.mode = runMode
        }
    }

    /**
     * Sets the direction of each motor in the MotorGroup
     *
     * @param direction the wanted direction
     */
    override fun setDirection(direction: DcMotorSimple.Direction) {
        for (motor in motors) {
            motor.direction = direction
        }
    }

    /**
     * These are some of the ways of getting an encoder position from a group of motors. The three
     * options here are averaging, adding, and just getting the first motor position.
     */
    enum class CurrentPositionMode {
        AVERAGE,
        ADD,
        FIRST_MOTOR
    }

    /**
     * This is a variable that can be set that contains what [CurrentPositionMode] is currently
     * being used. This defaults to [CurrentPositionMode.AVERAGE]
     */
    var currentPositionMode = CurrentPositionMode.AVERAGE

    override fun getCurrentPosition(): Int {
        when (currentPositionMode) {
            CurrentPositionMode.AVERAGE -> {
                var values = 0
                for (motor in motors) {
                    values += motor.currentPosition
                }
                return values / motors.size
            }
            CurrentPositionMode.ADD -> {
                var values = 0
                for (motor in motors) {
                    values += motor.currentPosition
                }
                return values
            }
            CurrentPositionMode.FIRST_MOTOR -> return motors[0].currentPosition
        }
    }

    override fun setTargetPosition(position: Int) {
        for (motor in motors) {
            motor.targetPosition = position
        }
    }

    fun getTargetPositions(): IntArray = IntArray(motors.size) {i -> motors[i].targetPosition}

    fun setTargetPositions(positions: IntArray) {
        if (positions.size != motors.size) {
            throw IllegalArgumentException("The list of target positions is not the same size as the" +
                    " list of motors")
        }

        for ((i, motor) in motors.withIndex()) {
            motor.targetPosition = positions[i]
        }
    }

    //These methods are mostly so that MotorGroup can be used anywhere a DcMotor is used as in input
    override fun close() {
        for (motor in motors) {
            motor.close()
        }
    }

    override fun getConnectionInfo(): String {
        val sb = StringBuilder()
        for (motor in motors) {
            sb.append(motor.connectionInfo)
        }
        return sb.toString()
    }

    override fun getController(): DcMotorController = motors[0].controller

    override fun getDeviceName(): String {
        val sb = StringBuilder()
        for (motor in motors) {
            sb.append(motor.deviceName)
        }
        return sb.toString()
    }

    /**
     * Gets the direction of the first motor in the MotorGroup
     */
    override fun getDirection(): DcMotorSimple.Direction = motors[0].direction

    /**
     * Gets the RunMode of the first motor in the MotorGroup
     */
    override fun getMode(): DcMotor.RunMode = motors[0].mode

    override fun getManufacturer(): HardwareDevice.Manufacturer = motors[0].manufacturer

    override fun getMotorType(): MotorConfigurationType = motors[0].motorType

        override fun getPortNumber(): Int = motors[0].portNumber

    override fun getPowerFloat(): Boolean = motors[0].powerFloat

    override fun getVersion(): Int = motors[0].version

    override fun isBusy(): Boolean {
        var isBusy = false
        for (motor in motors) {
            isBusy = isBusy || motor.isBusy
        }
        return isBusy
    }

    override fun getZeroPowerBehavior(): DcMotor.ZeroPowerBehavior = motors[0].zeroPowerBehavior

    override fun resetDeviceConfigurationForOpMode() {
        for (motor in motors) {
            motor.resetDeviceConfigurationForOpMode()
        }
    }

    override fun setMotorType(motorType: MotorConfigurationType?) {
        for (motor in motors) {
            motor.motorType = motorType
        }
    }

    override fun getTargetPosition(): Int = motors[0].targetPosition

    override fun setPowerFloat() {
        for (motor in motors) {
            motor.setPowerFloat()
        }
    }
}