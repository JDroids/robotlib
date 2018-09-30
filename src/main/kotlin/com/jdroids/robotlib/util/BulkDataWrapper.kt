package com.jdroids.robotlib.util

import com.qualcomm.hardware.lynx.*
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataCommand
import com.qualcomm.hardware.lynx.commands.core.LynxGetBulkInputDataResponse
import com.qualcomm.robotcore.hardware.*

/**
 * A wrapper around bulk data reads to make them more accessible.
 *
 * Note: This class uses reflection to break encapsulation. This may break if the internals of the
 * ftc sdk change. If this happens, please report an issue on the github page for robotlib.
 */
class BulkDataWrapper
/**
 * This creates a [BulkDataWrapper] given a LynxGetBulkDataResponse and a LynxModule
 *
 * @param response the response to read from
 * @param module the module to be used to retrieve the bulk data
 */(private val response: LynxGetBulkInputDataResponse, private val module: LynxModule) {

    /**
     * An alternate constructor in which no LynxGetBulkInputDataCommand has to be passed.
     *
     * @param module the module to be used to retrieve the bulk data and to create the
     *      LynxGetBulkInputDataCommand
     */
    constructor(module: LynxModule) : this(LynxGetBulkInputDataCommand(module).sendReceive(),
            module)


    /**
     * Returns the encoder position of a given DcMotor
     *
     * @param motor the motor who's velocity you want to get
     *
     * @return the encoder position of the motor
     */
    fun getMotorCurrentPosition(motor: DcMotor): Int = getMotorCurrentPosition(motor.portNumber)

    /**
     * Returns the encoder position of a motor of a given port.
     *
     * @param motorNum the port the motor is plugged into
     *
     * @return the encoder position of the motor plugged into the port
     */
    fun getMotorCurrentPosition(motorNum: Int): Int = response.getEncoder(motorNum)

    /**
     * Returns the velocity of a given DcMotor
     *
     * @param motor the motor who's velocity you want to get
     *
     * @return the motor's velocity (signed) in encoder counts per second
     */
    fun getMotorVelocity(motor: DcMotor): Int = getMotorVelocity(motor.portNumber)

    /**
     * Returns the velocity of a motor of a given port.
     *
     * @param motorNum the port the motor is plugged into
     *
     * @return the motor's velocity (signed) in encoder counts per second
     */
    fun getMotorVelocity(motorNum: Int): Int = response.getVelocity(motorNum)

    /**
     * Returns whether or not a motor passed to it is at its target position.
     *
     * @param motor the motor to check whether or not it is at its target position
     *
     * @return whether or not the passed motor is at its target position
     */
    fun isMotorAtTargetPosition(motor: DcMotor): Boolean {
        validateMotor(motor)
        return isMotorAtTargetPosition(motor.portNumber)
    }

    /**
     * Returns whether or not a moto
     * r of a given port is at its target position.
     *
     * @param motorNum the port the motor is plugged into
     *
     * @return whether or not the motor is at its target position
     */
    fun isMotorAtTargetPosition(motorNum: Int): Boolean = response.isAtTarget(motorNum)

    /**
     * Returns the value of an AnalogInput passed to it in volts.
     *
     * @param analogInput the DigitalChannel to get value of
     *
     * @return the value of the AnalogInput in volts
     */
    fun getAnalogInputValue(analogInput: AnalogInput): Double {
        if (!analogInput.isControllerRevHub()) {
            throw BulkDataReaderWrapperException("AnalogInput " +
                    "${getNameOfHardwareDevice(analogInput)} is not attached to a LynxModule!")
        }
        if (!validateLynxModule(analogInput.getController() as LynxDigitalChannelController)) {
            throw BulkDataReaderWrapperException("DigitalChannel " +
                    "${getNameOfHardwareDevice(analogInput)} is attached to a different Lynx " +
                    "module than the one that this bulk command was attached to a different rev " +
                    "hub than it is issued to!")
        }

        return getAnalogInputValue(analogInput.getPort())
    }

    /**
     * Returns the value of a AnalogInput of a given port in volts.
     *
     * @param port the port the AnalogInput is plugged into
     *
     * @return the value of the AnalogInput in volts
     */
    fun getAnalogInputValue(port: Int): Double = response.getAnalogInput(port).toDouble() * 1000


    /**
     * Returns the state of a DigitalChannel passed to it
     *
     * @param digitalChannel the DigitalChannel to get the state of.
     *
     * @return the state of the digital channel
     */
    fun getDigitalChannelState(digitalChannel: DigitalChannel): Boolean {
        if (!digitalChannel.isControllerRevHub()) {
            throw BulkDataReaderWrapperException("DigitalChannel " +
                    "${getNameOfHardwareDevice(digitalChannel)} is not attached to a LynxModule!")
        }
        if (!validateLynxModule(digitalChannel.getController() as LynxDigitalChannelController)) {
            throw BulkDataReaderWrapperException("DigitalChannel " +
                    "${getNameOfHardwareDevice(digitalChannel)} is attached to a different Lynx " +
                    "module than the one that this bulk command was attached to a different rev " +
                    "hub than it is issued to!")
        }

        return getDigitalChannelState(digitalChannel.getPort())
    }

    /**
     * Returns the state of a DigitalChannel of a given port.
     *
     * @param port the port the DigitalChannel is plugged into
     *
     * @return the state of the digital channel
     */
    fun getDigitalChannelState(port: Int): Boolean = response.getDigitalInput(port)


    /**
     * This function makes sure that the DcMotor passed to it is indeed plugged into a rev hub and
     * that aforementioned rev hub is the rev hub passed in the constructor.
     */
    private fun validateMotor(motor: DcMotor) {
        if (motor.controller !is LynxDcMotorController) {
            throw BulkDataReaderWrapperException("Motor ${getNameOfHardwareDevice(motor)} is not " +
                    "attached to a LynxModule!")
        }
        if (!validateLynxModule(motor.controller as LynxDcMotorController)) {
            throw BulkDataReaderWrapperException("Motor ${getNameOfHardwareDevice(motor)} is " +
                    "attached to a different Lynx module than the one that this bulk command was " +
                    "issued to!")
        }
    }

    /**
     * Validates the Lynx Module of a given LynxController
     */
    private fun validateLynxModule(controller: LynxController): Boolean =
            validateLynxModule(controller.getModule())

    /**
     * This function checks that the rev hub passed to it is the rev hub passed in the constructor
     *
     * @return whether or not the rev hub passed to it is the rev hub passed in the constructor
     */
    private fun validateLynxModule(moduleToValidate: LynxModule): Boolean =
            moduleToValidate.moduleAddress == module.moduleAddress

    /**
     * Returns the module of a LynxController
     */
    private fun LynxController.getModule(): LynxModule = javaClass.
            getDeclaredField("getModule").let {
        it.isAccessible = true
        return@let it.get(this) as LynxModule
    }

    /**
     * Returns whether or not the controller of an AnalogInput is a rev hub
     */
    private fun AnalogInput.isControllerRevHub(): Boolean =
            this.getController() is LynxAnalogInputController

    /**
     * Returns the controller of an AnalogInput through reflection
     */
    private fun AnalogInput.getController(): AnalogInputController =
            javaClass.getDeclaredField("controller").let {
        it.isAccessible = true
        return@let it.get(this) as AnalogInputController
    }

    /**
     * Gets the port the analog input is plugged into
     */
    private fun AnalogInput.getPort(): Int = javaClass.getDeclaredField("channel").let {
        it.isAccessible = true
        return@let it.getInt(this)
    }

    /**
     * Returns whether or not the controller of a DigitalChannel is a rev hub
     */
    private fun DigitalChannel.isControllerRevHub(): Boolean =
            this.getController() is LynxDigitalChannelController

    /**
     * Returns the controller of a DigitalChannel through reflection
     */
    private fun DigitalChannel.getController(): DigitalChannelController =
            javaClass.getDeclaredField("controller").let {
        it.isAccessible = true
        return@let it.get(this) as DigitalChannelController
    }

    /**
     * Gets the port the digital channel is plugged into
     */
    private fun DigitalChannel.getPort(): Int = javaClass.getDeclaredField("channel").let {
        it.isAccessible = true
        return@let it.getInt(this)
    }

    /**
     * This exception is thrown if the user tries to access data from a [BulkDataWrapper]
     * object that was received from a different LynxModule than the one that the bulk command was
     * issued to.
     */
    private class BulkDataReaderWrapperException(msg: String) : RuntimeException(msg) {
        constructor() : this("")
    }
}