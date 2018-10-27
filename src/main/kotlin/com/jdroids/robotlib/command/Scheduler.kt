package com.jdroids.robotlib.command

interface Scheduler {
    fun register(subsystem: Subsystem)

    fun run(command: Command)

    fun periodic()

    fun requires(command: Command, subsystem: Subsystem)
}