package com.jdroids.robotlib.command

import android.util.Log
import com.jdroids.robotlib.gamepad.EnhancedGamepad
import com.jdroids.robotlib.util.Set
import java.util.*

/**
 * The [Scheduler] is a singleton which holds the top-level running commands. It is in charge of
 * both calling the commands [run][Command.run] function and to make sure that there are no two
 * commands with conflicting requirements running.
 *
 * It is fine if teams wish to take control of the [Scheduler] themselves, all that needs to be done
 * is call the [Scheduler.run()][Scheduler.run] function often to have [commands][Command] function
 * properly. However, this is already done for you if you use the CommandBased RobotTemplate template.
 *
 * @see Command
 */
object Scheduler {
    /**
     * A hashtable of active [commands][Command] to their [LinkedListElement]
     */
    private val commandTable = Hashtable<Command, LinkedListElement>()
    /**
     * The [Set] of all [Subsystems][Subsystem]
     */
    private val subsystems = Set<Subsystem>()

    /**
     * The [Set] of all [EnhancedGamepads][EnhancedGamepad]
     */
    private val gamepads = Set<EnhancedGamepad>()
    /**
     * A set of all the [RobotTemplates][RobotTemplate]
     */
    private val robots = Set<RobotTemplate>()
    /**
     * The first [Command] in the list
     */
    private var firstCommand: LinkedListElement? = null
    /**
     * The last [Command] in the list
     */
    private var lastCommand: LinkedListElement? = null
    /**
     * Whether or not we are currently adding a command
     */
    private var adding = true
    /**
     * The list of all [commands][Command] which need to be added
     */
    private val additions = Vector<Command>()

    private var runningCommandsChanged = false

    /**
     * Whether or not we are currently disabled
     */
    private var disabled = true

    /**
     * Adds the command to the [Scheduler]. This will not add the [Command] immediately, but will
     * instead wait for the proper time in the [Scheduler.run] loop before doing so.
     *
     * Adding a [Command] to the [Scheduler] involves the [Scheduler] removing any [Command] which
     * has shared requirements.
     *
     * @param command the command to add
     */
    fun add(command: Command) {
        additions.add(command)
    }

    internal fun registerRobotTemplate(template: RobotTemplate) {
        this.robots.add(template)
    }

    /**
     * Adds a command immediately to the [Scheduler]. This should only be called in the
     * [Scheduler.run] loop. Any command with conflicting requirements will be removed, unless it is
     * uninterruptable. Giving `null` does nothing.
     *
     * @param command the {@link Command} to add
     */
    private fun addShadowMethod(command: Command?) {
        if (command == null) {
            return
        }

        //Check to make sure no adding during adding
        if (adding) {
            Log.e("Scheduler",
                    "WARNING: Can not start command from cancel method. Ignoring: $command")
            return
        }

        //Only add if not already in
        if (!commandTable.containsKey(command)) {
            //Check that the requirements can be had
            var requirements = command.getRequirements()
            while (requirements.hasMoreElements()) {
                val lock = requirements.nextElement()
                if (lock.getCurrentCommand() != null &&
                        !lock.getCurrentCommand()!!.isInterruptible()) {
                    return
                }
            }

            //Give it the requirements
            adding = true
            requirements = command.getRequirements()
            while (requirements.hasMoreElements()) {
                val lock = requirements.nextElement()
                if (lock.getCurrentCommand() != null) {
                    lock.getCurrentCommand()!!.cancel()
                    remove(lock.getCurrentCommand())
                }
                lock.setCurrentCommand(command)
            }
            adding = false

            //Add it to the list
            val element = LinkedListElement()
            element.setData(command)
            if (firstCommand == null) {
                firstCommand = element
                lastCommand = element
            }
            else {
                lastCommand!!.add(element)
                lastCommand = element
            }
            commandTable[command] = element

            runningCommandsChanged = true

            command.startRunning()
        }
    }

    /**
     * Runs a single iteration of the loop. This method should be called often in order to have a
     * functioning [Command] system.
     */
    fun run() {
        if (disabled) {
            //Don't run when disabled
            return
        }
        runningCommandsChanged = false

        //Update the controller values

        //Call every robot's periodic method
        val robotElements = robots.getElements()
        while (robotElements.hasMoreElements()) {
            robotElements.nextElement().periodic()
        }

        //Call every subsystem's periodic method
        val subsystemElements = subsystems.getElements()
        while (subsystemElements.hasMoreElements()) {
            subsystemElements.nextElement().periodic()
        }

        //Loop through the commands
        var element = firstCommand
        while (element != null) {
            val command = element.getData()
            element = element.getNext()

            if (!command!!.run()) {
                remove(command)
                runningCommandsChanged = true
            }
        }

        //Add the new things
        for (addition in additions) {
            addShadowMethod(addition)
        }
        additions.removeAllElements()

        //Add in the defaults
        val locks = subsystems.getElements()
        while (locks.hasMoreElements()) {
            val lock = locks.nextElement()

            if (lock.getCurrentCommand() == null) {
                addShadowMethod(lock.getDefaultCommand())
            }
            lock.confirmCommand()
        }
    }

    /**
     * Registers a [Subsystem] to the [Scheduler], so that the [Scheduler] might know if a default
     * [Command] needs to be run. All [subsystems][Subsystem] should call this.
     *
     * @param system the subsystem to be registered
     */
    internal fun registerSubsystem(system: Subsystem) {
        subsystems.add(system)
    }

    internal fun registerGamepad(gamepad: EnhancedGamepad) {
        gamepads.add(gamepad)
    }

    internal fun remove(command: Command?) {
        if (command == null || !commandTable.contains(command)) {
            return
        }
        val element = commandTable[command]

        commandTable.remove(command)

        if (element == lastCommand) {
            firstCommand = element!!.getPrevious()
        }
        if (element == firstCommand) {
            firstCommand = element!!.getNext()
        }
        element!!.remove()

        val requirements = command.getRequirements()
        while (requirements.hasMoreElements()) {
            requirements.nextElement().setCurrentCommand(null)
        }

        command.removed()
    }

    /**
     * Removes all commands.
     */
    fun removeAll() {
        while (firstCommand != null) {
            remove(firstCommand!!.getData())
        }
    }

    /**
     * Disable the command scheduler. Should be run at the end of an OpMode.
     */
    fun disable() {
        disabled = true
    }

    /**
     * Enable the command scheduler. Should be run at the beginning of an OpMode.
     */
    fun enable() {
        disabled = false
    }

    /**
     * Returns whether or not the scheduler is enabled.
     *
     * @return true if the command is enabled, false otherwise
     */
    fun isEnabled(): Boolean = !disabled

    fun getSubsystems(): Enumeration<Subsystem> = subsystems.getElements()

    /**
     * Returns whether or not all the commands are finished.
     *
     * @return true if the [Scheduler] has no more commands to run.
     */
    fun areAllCommandsCompleted(): Boolean = commandTable.isEmpty
}