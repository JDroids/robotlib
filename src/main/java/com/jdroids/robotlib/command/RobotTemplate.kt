package com.jdroids.robotlib.command

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl
import org.firstinspires.ftc.robotcore.internal.system.AppUtil

/**
 * [RobotTemplate] is a class designed to be a base for team's Robot classes for use as part of the
 * Command system. Each robot should instantiate each subsystem. The Robot class is also a candidate
 * for being made as a singleton, as you only have one robot.
 */
abstract class RobotTemplate {
    private val updateThread = UpdateThread()

    /**
     * This function is meant to initialize all of the subsystem's hardware. It is meant to be run
     * at the beginning of the OpMode.
     */
    private fun initHardware() {
        val opModeManager: OpModeManagerImpl =
                OpModeManagerImpl.getOpModeManagerOfActivity(AppUtil.getInstance().activity)

        val subsystems = Scheduler.getSubsystems()

        for (subsystem in subsystems) {
            subsystem.initHardware(opModeManager.activeOpMode.hardwareMap)
        }
    }

    /**
     * The init shadow method
     */
    private fun initShadowMethod() {
        initHardware()
        updateThread.start()
    }

    /**
     * This method is meant to be called at the beginning of each autonomous program, and should do
     * such things as initialize servo positions. If this is overridden, the method should always
     * run `super.autonomousInit`
     */
    open fun autonomousInit() {
        initShadowMethod()
    }

    /**
     * This method is meant to be called at the beginning of each teleop program. If this is
     * overridden, the method should always run `super.teleopInit()`. Please keep in mind it is
     * illegal to initialize servo positions in the init phase of teleop.
     */
    open fun teleopInit() {
        initShadowMethod()
    }



    private class UpdateThread : Thread() {
        val opModeManager: OpModeManagerImpl =
                OpModeManagerImpl.getOpModeManagerOfActivity(AppUtil.getInstance().activity)

        override fun run() {
            var activeOpMode = opModeManager.activeOpMode
            while (!Thread.currentThread().isInterrupted) {
                if (activeOpMode != null && activeOpMode != OpModeManagerImpl.DEFAULT_OP_MODE) {
                    if (Scheduler.isEnabled()) {
                        Scheduler.disable()
                    }
                }
                else {
                    if (!Scheduler.isEnabled()) {
                        Scheduler.enable()
                    }
                }
                Scheduler.run()
                activeOpMode = opModeManager.activeOpMode
            }
        }
    }
}