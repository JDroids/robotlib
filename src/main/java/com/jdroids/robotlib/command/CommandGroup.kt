package com.jdroids.robotlib.command

import java.util.*

/**
 * A [CommandGroup] is a list of commands that are executed in sequence.
 *
 * Commands in a [CommandGroup] are added using the [addSequential] method and are called
 * sequentially. [CommandGroups][CommandGroup] are themselves [commands][Command] and can be given
 * to other [CommandGroups][CommandGroup].
 *
 * [CommandGroups][CommandGroup] will carry all of the requirements of their [subcommands][Command].
 * Additional requirements can be specified by calling the [requires] function of the
 * [CommandGroup], normally in the constructor of the aforementioned [CommandGroup].
 *
 * [CommandGroups][CommandGroup] can also execute commands in parallel, simply by adding them using
 * [addParallel] rather than [addSequential].
 */
open class CommandGroup: Command {
    /**
     * The commands in this group (stored in entries).
     */
    private val commands = Vector<Entry>()

    /**
     * The active children in this group (stored in entries).
     */
    internal val children = Vector<Entry>()

    /**
     * The current command, -1 signifies that none have been run.
     */
    private var currentCommandIndex = -1

    /**
     * Creates a new [CommandGroup].
     */

    constructor()

    /**
     * Adds a new [Command] to the group. The [Command] will be started after all the previously
     * added [commands][Command].
     *
     * Note that any requirement the given [Command] has will be added to the group. For this
     * reason, a [command's][Command] requirements can not be changed after being added to a group.
     *
     * It is recommended that this method be called in the constructor.
     *
     * @param command the [command][Command] to be added
     *
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *      another group
     */
    @Synchronized
    final fun addSequential(command: Command) {
        validate("Can not add new command to command group")

        command.setParent(this)

        commands.addElement(Entry(command, Entry.IN_SEQUENCE))

        val requiredSubsystems = command.getRequirements()

        while (requiredSubsystems.hasMoreElements()) {
            requires(requiredSubsystems.nextElement())
        }
    }

    /**
     * Adds a new [Command] to the group with a given timeout. The [Command] will be started after
     * all the previously added [commands][Command].
     *
     * Once the [Command] is started, it will run until it finishes or the time expires, whichever
     * is sooner. Note that the given [Command] will have no knowledge that it is on a timer.
     *
     * Note that any requirement the given [Command] has will be added to the group. For this
     * reason, a [command's][Command] requirements can not be changed after being added to a group.
     *
     * It is recommended that this method be called in the constructor.
     *
     * @param command the [command][Command] to be added
     * @param timeout The timeout (in seconds)
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *      another group
     * @throws IllegalArgumentException if the timeout is less than 0
     */
    @Synchronized
    final fun addSequential(command: Command, timeout: Double) {
        validate("Can not add new command to command group")

        if (timeout < 0.0) {
            throw IllegalArgumentException("Can not be given a negative timeout")
        }

        command.setParent(this)

        commands.addElement(Entry(command, Entry.IN_SEQUENCE, timeout))

        val requiredSubsystems = command.getRequirements()

        while (requiredSubsystems.hasMoreElements()) {
            requires(requiredSubsystems.nextElement())
        }
    }

    /**
     * Adds a new child [Command] to the group. The [Command] will be started after all the
     * previously added [commands][Command].
     *
     * Instead of waiting for the child to finish, a [CommandGroup] will have it run at the same
     * time as the subsequent [commands][Command]. The child will run until either it finishes, a
     * new child with conflicting commands is started, or the main sequence runs a [Command] with
     * conflicting requirements. In the latter two cases, the child will be canceled even if it says
     * it can't be interrupted.
     *
     * Note that any requirement the given [Command] has will be added to the group. For this
     * reason, a [command's][Command] requirements can not be changed after being added to a group.
     *
     * It is recommended that this method be called in the constructor.
     *
     * @param command the [command][Command] to be added
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *      another group
     */
    @Synchronized
    final fun addParallel(command: Command) {
        validate("Can not add new command to command group")

        command.setParent(this)

        commands.addElement(Entry(command, Entry.BRANCH_CHILD))

        val requiredSubsystems = command.getRequirements()

        while (requiredSubsystems.hasMoreElements()) {
            requires(requiredSubsystems.nextElement())
        }
    }

    /**
     * Adds a new child [Command] to the group. The [Command] will be started after all the
     * previously added [commands][Command].
     *
     * Instead of waiting for the child to finish, a [CommandGroup] will have it run at the same
     * time as the subsequent [commands][Command]. The child will run until either it finishes, a
     * new child with conflicting commands is started, or the main sequence runs a [Command] with
     * conflicting requirements. In the latter two cases, the child will be canceled even if it says
     * it can't be interrupted.
     *
     * Note that any requirement the given [Command] has will be added to the group. For this
     * reason, a [command's][Command] requirements can not be changed after being added to a group.
     *
     * It is recommended that this method be called in the constructor.
     *
     * @param command the [command][Command] to be added
     * @param timeout The timeout (in seconds)
     * @throws IllegalUseOfCommandException if the group has been started before or been given to
     *      another group
     * @throws IllegalArgumentException if the timeout is less than 0
     */
    @Synchronized
    final fun addParallel(command: Command, timeout: Double) {
        validate("Can not add new command to command group")

        if (timeout < 0.0) {
            throw IllegalArgumentException("Can not be given a negative timeout")
        }

        command.setParent(this)

        commands.addElement(Entry(command, Entry.BRANCH_CHILD, timeout))

        val requiredSubsystems = command.getRequirements()

        while (requiredSubsystems.hasMoreElements()) {
            requires(requiredSubsystems.nextElement())
        }
    }

    override fun initializeShadowMethod() {
        currentCommandIndex = -1
    }

    override fun executeShadowMethod() {
        var entry: Entry? = null
        var cmd: Command? = null
        var firstRun = false

        if (currentCommandIndex == -1) {
            firstRun = true
            currentCommandIndex = 0
        }

        while (currentCommandIndex < commands.size) {
            if (cmd != null) {
                if (entry!!.isTimedOut()) {
                    cmd.cancelShadowMethod()
                }
                if (cmd.run()) {
                    break
                }
                else {
                    cmd.removed()
                    ++currentCommandIndex
                    /*
                    Yes, this is the right way to do this. If you have an issue with this, fix it in
                    a pull request if you decide to make one. But then something might happen to
                    your pull request.
                     */
                    firstRun = true
                    cmd = null
                    continue

                }
            }

            entry = commands.elementAt(currentCommandIndex)
            cmd = null

            when (entry.state) {
                Entry.IN_SEQUENCE -> {
                    cmd = entry.command
                    if (firstRun) {
                        cmd.startRunning()
                        cancelConflicts(cmd)
                    }
                    firstRun = false
                }
                Entry.BRANCH_PEER -> {
                    ++currentCommandIndex
                    entry.command.start()
                }
                Entry.BRANCH_CHILD -> {
                    ++currentCommandIndex
                    cancelConflicts(entry.command)
                    entry.command.startRunning()
                    children.addElement(entry)
                }
            }
        }

        //Run Children
        var i = 0
        while (i < children.size) {
            entry = children.elementAt(i)
            val child = entry.command
            if (entry.isTimedOut()) {
                child.cancelShadowMethod()
            }
            if (!child.run()) {
                child.removed()
                children.removeElementAt(i--)
            }
            ++i
        }
    }

    override fun endShadowMethod() {
        if (currentCommandIndex != -1 && currentCommandIndex < commands.size) {
            val cmd = commands.elementAt(currentCommandIndex).command
            cmd.cancelShadowMethod()
            cmd.removed()
        }

        val children = children.elements()

        while(children.hasMoreElements()) {
            val cmd = children.nextElement().command
            cmd.cancelShadowMethod()
            cmd.removed()
        }
        this.children.removeAllElements()
    }

    override fun interruptedShadowMethod() {
        endShadowMethod()
    }

    /**
     * Returns true if all the [commands][Command] in this group have been started and finished
     *
     * Teams may override this method, although they should probably reference super.isFinished() if
     * they do.
     *
     * @return whether this [CommandGroup] is finished.
     */
    override fun isFinished(): Boolean = currentCommandIndex >= commands.size && children.isEmpty()

    //Can be overwritten by teams
    override fun initialize() {}

    override fun execute() {}

    //Can be overwritten by teams
    override fun end() {}

    override fun interrupted() {}

    /**
     * Returns whether or not this group is interruptible. A command group will be uninterruptible
     * if [setInterruptible(false)][setInterruptible] was called or if it is currently running an
     * uninterruptible command or child.
     */
    override fun isInterruptible(): Boolean {
        if (!super.isInterruptible()) {
            return false
        }

        if (currentCommandIndex != -1 && currentCommandIndex < commands.size) {
            val cmd = commands.elementAt(currentCommandIndex).command
            if (!cmd.isInterruptible()) {
                return false
            }
        }

        for (i in 0 until children.size) {
            if (!children.elementAt(i).command.isInterruptible()) {
                return false
            }
        }

        return true
    }

    private fun cancelConflicts(command: Command) {
        var i = 0
        while (i < children.size) {
            val child = children.elementAt(i).command

            val requirements = command.getRequirements()

            while (requirements.hasMoreElements()) {
                val requirement = requirements.nextElement()
                if (child.doesRequire(requirement)) {
                    child.cancelShadowMethod()
                    child.removed()
                    children.removeElementAt(i--)
                    break
                }
            }

            ++i
        }
    }

    internal class Entry(val command: Command, val state: Int, val timeout: Double) {
        constructor(command: Command, state: Int) : this(command, state, -1.0)

        internal fun isTimedOut(): Boolean {
            if (timeout == -1.0) {
                return false
            }

            val time = command.timeSinceInitialized()
            return time != 0.0 && time >= timeout
        }

        companion object {
            internal val IN_SEQUENCE = 0
            internal val BRANCH_PEER = 1
            internal val BRANCH_CHILD = 2
        }
    }
}