package com.jdroids.robotlib.command

class ParallelCommandGroup(private vararg val commands: Command) : Command {
    override fun isInterruptible() =
            commands.all {c: Command -> c.isInterruptible()}

    override fun isCompleted() =
            commands.all {c: Command -> c.isCompleted()}

    override fun start() = commands.forEach {c -> c.start()}

    override fun periodic() = commands.forEach {c -> c.periodic()}

    override fun end() = commands.forEach {c -> c.end()}

    override fun interrupt() = commands.forEach {c -> c.interrupt()}
}