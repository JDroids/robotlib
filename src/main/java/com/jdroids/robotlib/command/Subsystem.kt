package com.jdroids.robotlib.command

import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * This class defines a major component of the robot.
 *
 * A good example of a subsystem is the drivetrain, or a claw if the robot has one.
 *
 * All actuators should be part of a subsystem. For instance, all the motors powering wheels should
 * be a part of some sort of a "Drivetrain" subsystem.
 *
 * Subsystems are used within the command system as requirements for [commands][Command]. Only one
 * command which requires a subsystem can run at a time. Also, subsystems can have default commands
 * which are started if there is no command running which requires this subsystem.
 *
 * @see Command
 */
abstract class Subsystem {
    /**
     * Can be used to initialize hardware using the lazy kotlin keyword
     */
    protected var hardwareMap: HardwareMap? = null

    /**
     * Whether or not [getDefaultCommand] was called.
     */
    private var initializedDefaultCommand = false

    /**
     * The current command.
     */
    private var currentCommand: Command? = null
    private var currentCommandChanged = false

    /**
     * The default command.
     */
    private var defaultCommand: Command? = null

    /**
     * Initialize the default command for a subsystem. By default, subsystems have no default
     * command, but if they do, the default command is set with this method. It is called on all
     * subsystems by CommandBase in the user's program after all the Subsystems are created.
     */
    protected abstract fun initDefaultCommand()

    /**
     * This method is designed to be where all of the hardware of the subsystem is initialized.
     * `super.initHardware(hardwareMap)` has to be called at the end of the implementation of this
     * function.
     *
     * @param hardwareMap the hardware map to initialize the hardware with
     */
    open fun initHardware(hardwareMap: HardwareMap) {
        this.hardwareMap = hardwareMap
        Scheduler.registerSubsystem(this)
    }

    /**
     * When the run function of the scheduler is called this method will be called
     */
    open fun periodic() {
        //Override this function!
    }

    /**
     * Sets the default command. If this is not called, there will be no default command for the
     * subsystem.
     *
     * WARNING: This should NOT be called in a constructor if the subsystem is a singleton. In
     * a Kotlin singleton, there is no constructor (without some really fancy tricks), so this
     * should be irrelevant.
     */
    fun setDefaultCommand(command: Command) {
        if (!command.doesRequire(this)) {
            throw IllegalUseOfCommandException("A default command must require the subsystem")
        }
        defaultCommand = command
    }

    /**
     * Returns the default command (or null if there is none)
     *
     * @return the default command
     */

    fun getDefaultCommand(): Command? {
        if (!initializedDefaultCommand) {
            initializedDefaultCommand = true
            initDefaultCommand()
        }
        return defaultCommand
    }

    /**
     * Returns the default command name, or empty string if there is none.
     *
     * @return the default command name
     */
    fun getDefaultCommandName(): String = getDefaultCommand()?.toString() ?: ""

    /**
     * Sets the current command
     *
     * @param command the new current command
     */
    internal fun setCurrentCommand(command: Command?) {
        currentCommand = command
        currentCommandChanged = true
    }

    /**
     * Call this to alert Subsystem the current command is actually the command. Sometimes, the
     * [Subsystem] is told that it has no command while the {@link Scheduler} is going through the
     * loop, only to be soon after given a new one. This will avoid that situation.
     */
    internal fun confirmCommand() {
        if (currentCommandChanged) {
            currentCommandChanged = false
        }
    }

    /**
     * Returns the command which currently claims this subsystem.
     *
     * @return the command which currently claims this subsystem
     */
    fun getCurrentCommand(): Command? {
        return currentCommand
    }

    /**
     * Returns the current command name, or empty string if there is none.
     *
     * @return the default command name
     */
    fun getCurrentCommandName(): String = getCurrentCommand()?.toString() ?: ""

    override fun toString(): String = this.javaClass.toString()

    init {
        currentCommandChanged = true
    }
}