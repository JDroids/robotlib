package com.jdroids.robotlib.command

/**
 * The [Subsystem] interface represents a physical component of a robot. These
 * can be a drivetrain, a claw, etc. They should be
 * [registered][Scheduler.register] at some point.
 */
interface Subsystem {
    /**
     * This method should initialize the hardware of the component. This is
     * typically getting all the hardware devices from hardware map.
     */
    fun initHardware()

    /**
     * This method is called by [Scheduler.periodic] as long as the subsystem is
     * registered.
     */
    fun periodic()
}