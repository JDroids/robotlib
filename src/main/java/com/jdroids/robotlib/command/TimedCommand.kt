package com.jdroids.robotlib.command

/**
 * A [TimedCommand] will wait for a timeout before finishing. [TimedCommand] is used to execute a
 * command for a given amount of time.
 */
open class TimedCommand
/**
 * Instantiates a TimedCommand with the given timeout.
 *
 * @param timeout the time the command takes to run (seconds)
 */(timeout: Double) : Command(timeout) {

    /**
     * Ends command when timed out.
     *
     */
    override fun isFinished(): Boolean = isTimedOut()
}