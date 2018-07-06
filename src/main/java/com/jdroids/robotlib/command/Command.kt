package com.jdroids.robotlib.command

import com.jdroids.robotlib.util.Set
import com.qualcomm.robotcore.util.ElapsedTime
import java.util.Enumeration

/**
 * The [Command] class is the center of the Command framework. Every command can be started by calling
 * [start]. Once a command is called [initialize] will be called and then [execute] will be
 * called over and over until [isFinished] returns true. Once it does, [end] will be called.
 *
 * If at any point [cancel] is called however, the command will be stopped and [interrupted]
 * will be called.
 *
 * If a command uses a [Subsystem], it should specify it does so by calling the [requires] method
 * in its constructor. A command may have multiple requirements, and [requires] should be used for
 * each one.
 *
 * If a command is running and a new command with shared requirements is started, then one of two
 * things will happen. If the active command is interruptible, then [cancel] will be called and
 * the command will be removed to make way for the new one. If the active command is not
 * interruptible, the other one will not be started, and the active one will continue functioning.
 */
abstract class Command {
    /**
     * A timer to keep track of how much time has passed since the command has been initialized
     */
    private var timer = ElapsedTime()
    private var isTimerStarted = false

    /**
     * The time in seconds before the command "times out", or -1.0 if no timeout
     */
    private var timeout = -1.0

    /**
     * Whether or not the command has been initialized
     */
    private var initialized = false

    /**
     * The required subsystems
     */
    private val requirements = Set<Subsystem>()

    /**
     * Whether or not the command is running
     */
    private var running = false

    /**
     * Whether or not the command is interruptible
     */
    private var interruptible = true

    /**
     * Whether or not the command has been canceled
     */
    private var canceled = false

    /**
     * Whether or not the command has been locked
     */
    private var locked = false

    /**
     * Whether or not the command has completed running
     */
    private var completed = false

    /**
     * The CommandGroup this command is in
     */
    private var parent: CommandGroup? = null

    /**
     * Creates a new command.
     */
    constructor()


    /**
     * Creates a command with the given timeout.
     *
     * @param timeout the time in seconds before the command "times out".
     * @throws IllegalArgumentException if the given timeout is negative
     */
    constructor(timeout: Double) {
        if (timeout < 0.0) {
            throw IllegalArgumentException("Timeout must not be negative. Given: $timeout")
        }
        this.timeout = timeout
    }

    /**
     * Sets the timeout of this command
     *
     * @param seconds the timeout (in seconds)
     * @throws IllegalArgumentException if the given timeout is negative
     */
    @Synchronized
    protected fun setTimeout(seconds: Double) {
        if (seconds < 0) {
            throw IllegalArgumentException("Seconds must be positive. Given:$seconds")
        }
        timeout = seconds
    }

    /**
     * Returns the time since the command has been initialized (in seconds). This function will work
     * even if there is no timeout
     *
     * @return the time since this command was initialized (in seconds)
     */
    @Synchronized
    fun timeSinceInitialized(): Double = if(isTimerStarted) timer.seconds() else 0.0

    /**
     * This method specifies that the given [Subsystem] is used by this command. This method is
     * crucial to the functioning of the Command System in general.
     *
     * Note that the recommended way to call this method is in the constructor.
     *
     * @param subsystem the required [Subsystem]
     * @throws IllegalUseOfCommandException if this command has been started before or if it has
     *                                      been given to a command group
     */
    @Synchronized
    protected fun requires(subsystem: Subsystem) {
        validate("Can not add new requirement to command")
        requirements.add(subsystem)
    }

    /**
     * Called when the command has been removed. This will call [interrupted] or [end].
     */
    @Synchronized
    internal fun removed() {
        if (initialized) {
            if (isCanceled()) {
                interrupted()
                interruptedShadowMethod()
            }
            else {
                end()
                endShadowMethod()
            }
        }
        initialized = false
        canceled = false
        running = false
        completed = true
    }

    /**
     * The run method is used internally to actually run the commands.
     *
     * @return whether or not the command should remain in the [Scheduler]
     */
    @Synchronized
    internal fun run(): Boolean {
        if(isCanceled()) {
            return false
        }
        if (!initialized) {
            initialized = true
            startTiming()
            initializeShadowMethod()
            initialize()
        }
        executeShadowMethod()
        execute()
        return !isFinished()
    }

    /**
     * The initialize method is called the first time this Command is run after being started.
     */
    protected open fun initialize() {}

    /**
     * A shadow method called before [initialize]
     */
    internal open fun initializeShadowMethod() {}

    /**
     * The execute command is called repeatedly until this command either finishes or is canceled.
     */
    protected open fun execute() {}

    /**
     * A shadow method called before [execute]
     */
    internal open fun executeShadowMethod() {}

    /**
     * Returns whether this command is finished. If it is, then the command will be removed and
     * [end] will be called.
     *
     * It may be useful to reference [isTimedOut()] for time-sensitive commands.
     *
     * Returning false will result in the command never ending automatically. It may still be
     * canceled manually or interrupted by another command. Returning true will result in the
     * command executing once and finishing immediately. It is recommended to use [InstantCommand]
     * for that.
     */
    protected abstract fun isFinished(): Boolean

    /**
     * Called when the command ended peacefully. This is where you may want to wrap up loose ends,
     * like shutting off a motor that was being used in the command.
     */
    protected open fun end() {}

    /**
     * A shadow method called after [end]
     */
    protected open fun endShadowMethod() {}

    /**
     * Called when the command ends because something called [cancel] or another command shared the
     * same requirements as this one and booted this one out.
     *
     * This is where you may want to wrap up loose ends, like shutting off a motor that was being
     * used in the command.
     *
     * Generally, it is useful to simply call the [end] function within this function, as done here
     */
    protected open fun interrupted() {
        end()
    }

    /**
     * A shadow method called after [interrupted]
     */
    protected open fun interruptedShadowMethod() {}

    /**
     * Called to indicate that the timer should start. This is called right before [initialize] is,
     * inside the [run] method
     */
    private fun startTiming() {
        timer.reset()
    }

    /**
     * Returns whether or not the [timeSinceInitialized] method returns a number which is greater
     * than or equal to the timeout for the command. If there is no timeout, this will always return
     * false.
     *
     * @return whether the time has expired
     */
    @Synchronized
    protected fun isTimedOut(): Boolean = timeout != -1.0 && timeSinceInitialized() >= timeout

    /**
     * Returns the requirements (as an [Enumeration] of [Subsystems][Subsystem]) of this command.
     *
     * @return the requirements of this command (as a [Enumeration] of [Subsystems][Subsystem])
     */
    @Synchronized
    internal fun getRequirements(): Enumeration<Subsystem> = requirements.getElements()

    /**
     * Prevents further changes from being made.
     */
    @Synchronized
    internal fun lockChanges() {
        locked = true
    }

    @Synchronized
    internal fun validate(message: String) {
        if (locked) {
            throw IllegalUseOfCommandException("$message after being started or being added to a " +
                    "command group")
        }
    }

    /**
     * Sets the parent of this command. No actual change is made to the group.
     *
     * @param parent the parent
     * @throws IllegalUseOfCommandException if this [Command] is already in a [CommandGroup]
     */
    @Synchronized
    internal fun setParent(parent: CommandGroup) {
        if (this.parent != null) {
           throw IllegalUseOfCommandException("Can not give a command to a command group after " +
                   "already being put in a command group")
        }
        lockChanges()
        this.parent = parent
    }

    /**
     * Returns whether the command has a parent
     *
     * @return true if the command has a parent
     */
    @Synchronized
    internal fun isParented(): Boolean = parent != null

    /**
     * Clears the list of subsystem requirements. This is only used by [ConditionalCommand] so
     * cancelling the chosen command works properly in [CommandGroup].
     */
    internal fun clearRequirements() {
        requirements.clear()
    }

    /**
     * Starts up the command. Gets the command ready to start.
     *
     * Note that the command will eventually start, however it will not necessarily do so
     * immediately, and may be canceled before initialize is even called.
     *
     * @throws IllegalUseOfCommandException if the the command is part of a [CommandGroup]
     */
    @Synchronized
    fun start() {
        lockChanges()
        if (parent != null) {
            throw IllegalUseOfCommandException("Can not start a command that is part of a command" +
                    " group")
        }
        Scheduler.add(this)
        completed = false
    }

    /**
     * This is used internally to mark that the command has been started. The lifecycle of the
     * command is:
     *
     * First, [startRunning] is called. Then, [run] is called (potentially multiple times). Finally,
     * [removed] is called.
     *
     * It is important that [startRunning] and [removed] be called in order or some assumptions of
     * the code will be broken.
     */
    @Synchronized
    internal fun startRunning() {
        running = true
        isTimerStarted = false
    }

    /**
     * Returns whether or not the command is running. This may return true even if the command has
     * just been canceled, as it may not have yet called [interrupted].
     *
     * @return whether or not the command is running
     */
    @Synchronized
    fun isRunning(): Boolean = running

    /**
     * This will cancel the current [command][Command].
     *
     * This will cancel the current command eventually. It can be called multiple times. And it can
     * be called when the command is not running. If the command is running however, then the
     * command will be marked as canceled and eventually removed.
     *
     * A command can not be canceled if it is a part of a [command group][CommandGroup], you must
     * cancel te command group instead.
     *
     * @throws IllegalUseOfCommandException if this command is a part of a command group
     */
    @Synchronized
    fun cancel() {
        if (parent != null) {
            throw IllegalUseOfCommandException("Can not manually cancel a command in a command " +
                    "group")
        }
        cancelShadowMethod()
    }

    @Synchronized
    internal open fun cancelShadowMethod() {
        if (isRunning()) {
            canceled = true
        }
    }

    /**
     * Returns whether or not this [command][Command] has been canceled.
     *
     * @return whether or not this has been canceled
     */
    @Synchronized
    fun isCanceled(): Boolean = canceled

    /**
     * Returns whether or not this [command][Command] has been completed.
     *
     * @return whether or not this has been completed
     */
    @Synchronized
    fun isCompleted(): Boolean = completed

    /**
     * Returns whether or not this [command][Command] has been interruptible.
     *
     * @return whether or not this has been interruptible
     */
    @Synchronized
    open fun isInterruptible(): Boolean = interruptible

    /**
     * Sets whether or not this command can be interrupted.
     *
     * @param interruptible whether or not this command can be interrupted
     */
    @Synchronized
    protected fun setInterruptible(interruptible: Boolean) {
        this.interruptible = interruptible
    }

    /**
     * Check if the command requires the given [Subsystem]
     *
     * @param system the subsystem to check
     * @return whether or not the subsystem is required
     */
    @Synchronized
    fun doesRequire(system: Subsystem): Boolean = requirements.contains(system)

    /**
     * Returns the [CommandGroup] that this command is part of. Will return null if this
     * [command][Command] is not in a group.
     *
     * @return the [CommandGroup] that this command is a part of (or null if this command is not in a group
     */
    @Synchronized
    fun getGroup(): CommandGroup? = parent

    /**
     * The string representation for a [Command] is its class name.
     *
     * @return the class name of the command
     */
    override fun toString(): String = this.javaClass.name
}

