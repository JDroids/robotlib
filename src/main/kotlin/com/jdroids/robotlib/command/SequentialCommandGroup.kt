package com.jdroids.robotlib.command

/**
 * A [SequentialCommandGroup] is intended to run multiple commands one at the
 * time.
 *
 * @param commands the commands to run
 */
class SequentialCommandGroup(private vararg val commands: Command) : Command {
    private var currentIndex = 0

    /**
     * This method returns whether or not the command group should be
     * interruptible.
     *
     * @return if the command group should be interruptible
     */
    override fun isInterruptible() =
            commands.all {c: Command -> c.isInterruptible()}

    /**
     * This method returns if the command group has finished running.
     *
     * @return if the command group finished running
     */
    override fun isCompleted() = commands[commands.size-1].isCompleted()

    /**
     * Starts the first command.
     */
    override fun start() = commands[0].start()

    /**
     * Runs the [Command.periodic] function of the correct [Command].
     */
    override fun periodic() {
        if (currentIndex < commands.size) {
            val currentCommand = commands[currentIndex]

            if (currentCommand.isCompleted()) {
                currentCommand.end()
                ++currentIndex
                commands[currentIndex].start()
            }
            else {
                currentCommand.periodic()
            }
        }
    }

    /**
     * Calls the [end] function of the currently running [Command].
     */
    override fun end() = commands[currentIndex].end()

    /**
     * Calls the [interrupt] function of the currently running [Command].
     */
    override fun interrupt() = commands[currentIndex].interrupt()
}