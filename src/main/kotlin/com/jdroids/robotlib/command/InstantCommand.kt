package com.jdroids.robotlib.command

/**
 * This command will execute once, then finish immediately afterward.
 *
 * Subclassing [InstantCommand] is shorthand for returning true from
 * [Command.isFinished]
 */
abstract class InstantCommand() : Command() {
    override fun isFinished(): Boolean {
        return true
    }
}