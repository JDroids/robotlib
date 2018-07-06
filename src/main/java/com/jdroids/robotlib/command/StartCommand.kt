package com.jdroids.robotlib.command

/**
 * A [StartCommand] will call the [Command.start] method of another command
 * when it is initialized and will finish immediately.
 */
class StartCommand
/**
 * Instantiates a [StartCommand] which will start the given command whenever its
 * [initialize()][Command.initialize] function is called.
 *
 * @param commandToStart the [Command] to start
 */(commandToStart: Command) : InstantCommand() {
    /**
     * The command to fork.
     */
    private val commandToFork: Command = commandToStart

    override fun initialize() {
        commandToFork.start()
    }
}