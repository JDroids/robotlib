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
            }
            else {
                c.periodic()
            }
        }

        runningCommands.removeAll {it.isCompleted()}

        subsystems.forEach {s: Subsystem -> s.periodic()}
    }
}