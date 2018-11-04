package com.jdroids.robotlib.command

/**
 * A [ParallelCommandGroup] is intended to run multiple commands at the same
 * time.
 *
 * @param commands the commands to run at the same time
 */
class ParallelCommandGroup(private vararg val commands: Command) : Command {
    /**
     * This method returns whether or not the command group should be
     * interruptible.
     *
     * @return if the command group should be interruptible
     */
    override fun isInterruptible() =
            commands.all {it.isInterruptible()}

    /**
     * This method returns if the command group has finished running.
     *
     * @return if the command group finished running
     */
    override fun isCompleted() =
            commands.all {it.isCompleted()}

    /**
     * Calls the [Command.start] method of each command within the command
     * group.
     */
    override fun start() = commands.forEach {it.start()}

    /**
     * Calls the [Command.periodic] method of each command within the command
     * group.
     */
    override fun periodic() = commands.forEach {it.periodic()}

    /**
     * Calls the [Command.end] method of each command within the command group.
     */
    override fun end() = commands.forEach {it.end()}

    /**
     * Calls the [Command.interrupt] method of each command within the command
     * group.
     */
    override fun interrupt() = commands.forEach {it.interrupt()}
}