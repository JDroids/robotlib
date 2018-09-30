package com.jdroids.robotlib.command

/**
 * This command will only finish if whatever [CommandGroup] it is in has no active children.
 * If it is not a part of a [CommandGroup], then it will finish immediately. If it is itself
 * an active child, then the [CommandGroup] will never end.
 *
 * This class is useful for the situation where you want to allow anything running in parallel
 * to finish, before continuing in the main [CommandGroup] sequence.
 */
class WaitForChildren : Command() {
    override fun isFinished(): Boolean = getGroup()?.children?.isEmpty() == true
}