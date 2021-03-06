package com.jdroids.robotlib.command

/**
 * The [Command] interface is the center of the command-based design pattern.
 * Each command represents a certain action.
 */
interface Command {
    /**
     * This function tells the [Scheduler] if the command is completed.
     *
     * @return if the command is completed
     */
    fun isCompleted(): Boolean

    /**
     * This function is called once whenever [Scheduler.run] is called on a
     * command. This typically initializes things for the command.
     */
    fun start()

    /**
     * This function is called by [Scheduler.periodic] until [isCompleted]
     * returns true. This typically updates motor positions, servo positions,
     * did logic, etc.
     */
    fun periodic()

    /**
     * This is called once by [Scheduler.periodic] when [isCompleted] returns
     * true. This typically stops motors moving, resets servos, etc.
     */
    fun end()
}