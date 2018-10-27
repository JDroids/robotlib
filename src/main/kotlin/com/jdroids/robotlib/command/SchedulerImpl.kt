package com.jdroids.robotlib.command

import kotlin.collections.HashSet

object SchedulerImpl : Scheduler {
    private val subsystems = HashSet<Subsystem>()

    override fun register(subsystem: Subsystem) {
        subsystems.add(subsystem)
    }

    private val runningCommands = HashSet<Command>()

    override fun run(command: Command) {
        command.start()
        runningCommands.add(command)
    }

    override fun periodic() {
        runningCommands.forEach { c ->
            if (c.isCompleted()) {
                c.end()
                runningCommands.remove(c)
            } else {
                c.periodic()
            }
        }

        subsystems.forEach {s: Subsystem -> s.periodic()}
    }
    private val commandRequirements = HashMap<Command, HashSet<Subsystem>>()

    @Synchronized
    override fun requires(command: Command, subsystem: Subsystem) {
        commandRequirements.keys.removeAll {
            if (commandRequirements[it]?.contains(subsystem) == true) {
                if (it.isInterruptible()) {
                    it.interrupt()
                    true
                }
                else {
                    throw IllegalStateException("Command started with same " +
                            "requirement as another, uninterruptible command")
                }
            }
            false
        }

        runningCommands.removeAll {it.isInterruptible() &&
                commandRequirements[it]?.contains(subsystem) == true}

        if (commandRequirements[command] == null) {
            commandRequirements[command] = HashSet()
        }

        commandRequirements[command]!!.add(subsystem)
    }
}