package com.jdroids.robotlib.command

interface Command {
    fun isInterruptible(): Boolean

    fun isCompleted(): Boolean

    fun start()

    fun periodic()

    fun end()

    fun interrupt()
}