package com.jdroids.robotlib.drive

import com.jdroids.robotlib.util.MotorGroup
import com.qualcomm.robotcore.hardware.DcMotor
import com.jdroids.robotlib.util.MathUtil
import com.qualcomm.robotcore.hardware.DcMotorSimple

/**
 * A class for driving differential drive robots. Differential drive is also known as tank drive,
 * skid-steer, and more.
 *
 * @param leftMotorGroup the collection of left motors
 * @param rightMotorGroup the collection of right motors
 * @param reverseLeftMotorGroup whether or not to automatically reverse the direction left motor
 *                              group. (defaults to true)
 *                              Note: When this variable is true, the class assumes the motors have
 *                              to go the same way on each side to move in a specific direction, but
 *                              this can be disabled if you have a special setup where two motors on
 *                              one side have to go in different directions. In that case, this
 *                              parameter has to be turned to false and all the motors within the
 *                              motor group have to be manually set to the right direction so that a
 *                              power of 1.0 on all motor groups would move the robot forwards at
 *                              max speed.
 * @param squareInputs whether or not to square inputs for motion so that the driver has finer
*                      controls at lower speeds (defaults to true)
 */
class DifferentialDrive(private val leftMotorGroup: MotorGroup,
                        private val rightMotorGroup: MotorGroup,
                        reverseLeftMotorGroup: Boolean = true,
                        squareInputs: Boolean = true): RobotDriveBase(squareInputs) {
    private var quickStopAccumulator = 0.0

    init {
        if (reverseLeftMotorGroup) {
            leftMotorGroup.setDirection(DcMotorSimple.Direction.REVERSE)
        }

        leftMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT)
        rightMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT)
    }

    /**
     * The secondary constructor for the DifferentialDrive class in the case that each side only
     * contains one motor.
     *
     * @param leftMotor the motor on the left side of the bot
     * @param rightMotor the motor on the right side of the bot
     * @param reverseLeftMotor whether or not to automatically reverse the direction left motor.
     *                         (defaults to true)
     *                         Note: When this variable is true, the class assumes the motor have to
     *                         go the same way on each side to move a specific direction, but this
     *                         can be disabled if you have a special setup. In that case, this
     *                         parameter has to be turned to false and all the motors have to be
     *                         manually set to the right direction so that a power of 1.0 on all
     *                         motors would move the  robot forwards at max speed.
     * @param squareInputs whether or not to square inputs for motion so that the driver has finer
     *                     controls at lower speeds (defaults to true)
     */
    constructor(leftMotor: DcMotor, rightMotor: DcMotor, reverseLeftMotor: Boolean = true,
                squareInputs: Boolean = true) : this(MotorGroup(leftMotor), MotorGroup(rightMotor),
            reverseLeftMotor, squareInputs)
    /**
     * Tank drive method for differential drive platform.
     *
     * @param leftSpeed the robot's left speed along the x-axis [-1.0..1.0]. Forward is positive.
     * @param rightSpeed the robot's right speed along the x-axis [-1.0..1.0]. Forward is positive.
     */
    @Synchronized
    fun tankDrive(leftSpeed: Double, rightSpeed: Double) {
        leftMotorGroup.setPower(correctInputs(leftSpeed))
        rightMotorGroup.setPower(correctInputs(rightSpeed))
    }

    /**
     * Arcade drive method for differential drive platform.
     *
     * @param xSpeed forwards velocity
     * @param zRotation angular velocity
     */
    @Synchronized
    fun arcadeDrive(xSpeed: Double, zRotation: Double) {
        val xSpeedCorrected = correctInputs(xSpeed)
        val zRotationCorrected = correctInputs(zRotation)

        val maxInput = Math.copySign(Math.max(Math.abs(xSpeedCorrected),
                Math.abs(zRotationCorrected)), xSpeedCorrected)

        val leftMotorOutput: Double
        val rightMotorOutput: Double

        if (xSpeed >= 0) {
            //First quadrant, else second quadrant
            if (zRotationCorrected >= 0) {
                leftMotorOutput = maxInput
                rightMotorOutput = xSpeedCorrected - zRotationCorrected
            }
            else {
                leftMotorOutput = xSpeedCorrected + zRotationCorrected
                rightMotorOutput = maxInput
            }
        }
        else {
            //Third quadrant, else fourth quadrant
            if (zRotationCorrected >= 0) {
                leftMotorOutput = xSpeedCorrected + zRotationCorrected
                rightMotorOutput = maxInput
            }
            else {
                leftMotorOutput = maxInput
                rightMotorOutput = xSpeedCorrected - zRotationCorrected
            }
        }

        leftMotorGroup.setPower(leftMotorOutput)
        rightMotorGroup.setPower(rightMotorOutput)
    }

    @Synchronized
    fun setBrakeMode(brake: Boolean) {
        if (brake) {
            leftMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
            rightMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
        }
        else {
            leftMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT)
            rightMotorGroup.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT)
        }
    }

    /**
     * Similar to arcade drive, but instead of turning at a certain velocity the robot travels at a
     * certain curvature.
     *
     * @param xSpeed the forwards velocity to travel at
     * @param zRotation the curvature at which to travel at
     * @param isQuickTurn if this is set to true, the robot no longer travels in a curve and instead\
     *                    turns in place
     */
    @Synchronized
    fun curvatureDrive(xSpeed: Double, zRotation: Double, isQuickTurn: Boolean) {
        val xSpeedCorrected = correctInputs(xSpeed)
        val zRotationCorrected = correctInputs(zRotation)

        val angularPower: Double
        val overPower: Boolean

        if (isQuickTurn) {
            if (Math.abs(xSpeedCorrected) < 0.2) {
                val alpha = 0.1
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha *
                        MathUtil.limit(zRotationCorrected, 1.0) * 2
            }
            overPower = true
            angularPower = zRotationCorrected
        }
        else {
            overPower = false
            angularPower = Math.abs(xSpeedCorrected) * zRotationCorrected - quickStopAccumulator
            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1
                quickStopAccumulator < -1 -> quickStopAccumulator += 1
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = xSpeedCorrected + angularPower
        var rightMotorOutput = xSpeedCorrected - angularPower

        //If rotation is overpowered, reduce both outputs to allowable range
        if (overPower) {
            when {
                leftMotorOutput > 1.0 -> {
                    rightMotorOutput -= leftMotorOutput - 1.0
                    leftMotorOutput = 1.0
                }
                rightMotorOutput > 1.0 -> {
                    leftMotorOutput -= rightMotorOutput - 1.0
                    rightMotorOutput = 1.0
                }
                leftMotorOutput < -1.0 -> {
                    rightMotorOutput -= leftMotorOutput + 1.0
                    leftMotorOutput = -1.0
                }
                rightMotorOutput < -1.0 -> {
                    leftMotorOutput -= rightMotorOutput + 1.0
                    rightMotorOutput = -1.0
                }
            }
        }

        leftMotorGroup.setPower(leftMotorOutput)
        rightMotorGroup.setPower(rightMotorOutput)    }

    override fun toString(): String =
            "Left Power: ${leftMotorGroup.getPower()}, Right Power: ${rightMotorGroup.getPower()}"
}