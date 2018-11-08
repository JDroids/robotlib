package com.jdroids.robotlib.command

/**
 * A fairly simple implementation of [Scheduler].
 */
object SchedulerImpl : Scheduler {
    private class HardwareUpdateThread : Thread() {
        override fun run() {
            while (!this.isInterrupted) {
                subsystems.forEach {it.periodic()}
            }
        }
    }

    private val updateThread = HardwareUpdateThread()

    private val subsystems = HashSet<Subsystem>()


    /**
     * Registers a [Subsystem] so that it's [periodic][Subsystem.periodic]
     * method is called when [Scheduler.periodic] is called.
     *
     * @param subsystem the subsystem to register
     */
    override fun register(subsystem: Subsystem) {
        if (!updateThread.isAlive) {
            updateThread.start()
        }

        subsystems.add(subsystem)
    }

    private val runningCommands = HashSet<Command>()

    /**
     * [Starts][Command.start] a given [Command], and adds it to a queue so that
     * it's [periodic][Command.periodic] function is called when [periodic] is
     * called.
     *
     * @param command the command to run
     */
    override fun run(command: Command) {
        command.start()
        runningCommands.add(command)
    }

    /**
     * The [periodic] function updates the state of the [SchedulerImpl]. It
     * should be called as often as possible.
     */
    override fun periodic() {
        runningCommands.forEach { c ->
            if (c.isCompleted()) {
                c.end()
                clearSubsystemRequirements(c)
            }
            else {
                c.periodic()
            }
        }

        runningCommands.removeAll {it.isCompleted()}

        subsystems.forEach {s: Subsystem -> s.periodic()}
    }
    private val commandRequirements = HashMap<Command, HashSet<Subsystem>>()

    /**
     * Checks if a given [Command] can use a given [Subsystem].
     *
     * @param command the command to check
     * @param subsystem the subsystem to check
     */
    @Synchronized
    override fun requires(command: Command, subsystem: Subsystem) {
        commandRequirements.keys.removeAll {
            if (commandRequirements[it]?.contains(subsystem) == true) {
                if (it.isInterruptible()) {
                    it.interrupt()
                    return@removeAll true
                }
                else {
                    throw IllegalStateException("Command started with same " +
                            "requirement as another, uninterruptible command")
                }
            }
            return@removeAll false
        }

        runningCommands.removeAll {it.isInterruptible() &&
                commandRequirements[it]?.contains(subsystem) == true}

        if (commandRequirements[command] == null) {
            commandRequirements[command] = HashSet()
        }

        commandRequirements[command]!!.add(subsystem)
    }

    /**
     * This function should clear the [Subsystem] requirements of a given
     * [Command]. It should be called by [periodic] after it [ends][Command.end]
     * a [Command]. If an opmode ends for an unknown reason, it should be called
     * by the user.
     */
    override fun clearSubsystemRequirements(command: Command) {
        commandRequirements.remove(command)
    }
}