package com.jdroids.robotlib.command

/**
 * A [WaitCommand] will wait for a certain amount of time before finishing. It is useful if you want
 * a [CommandGroup] to pause for a moment.
 *
 * @see CommandGroup
 */
class WaitCommand
/**
 * Instantiates a [WaitCommand] with the given timeout.
 *
 * @param timeout the time the command takes to run (seconds)
 */(timeout: Double) : TimedCommand(timeout)