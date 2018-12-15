package com.jdroids.robotlib.command

/**
 * A [ParallelCommandGroup] is intended to run multiple commands at the same
 * time.
 *
 * @param commands the commands to run at the same time
 */
class ParallelCommandGroup(private vararg val commands: Command) : Command {
    private val completedCommands = HashSet<Command>()

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
    override fun periodic()  {
        for (command in commands) {
            if (command.isCompleted()) {
                if (!completedCommands.contains(command)) {
                    command.end()
                    completedCommands.add(command)
                }
            }
            else {
                command.periodic()
            }
        }
    }

    /**
     * Calls the [Command.end] method of each command within the command group.
     */
    override fun end() = commands.forEach {
        if (!completedCommands.contains(it)) it.end()}
}