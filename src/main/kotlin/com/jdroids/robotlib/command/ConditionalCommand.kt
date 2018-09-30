package com.jdroids.robotlib.command

/**
 * A [ConditionalCommand] is a [Command] that starts one of two commands.
 *
 * A [ConditionalCommand] uses [condition] to determine whether or it should run the
 * onTrue or onFalse command.
 *
 * If no command is specified for onFalse, the occurrence of that condition will be a no-op.
 */
abstract class ConditionalCommand : Command {
    /**
     * The command to execute if [condition] returns true.
     */
    private val onTrue: Command

    /**
     * The command to execute if [condition] returns false.
     */
    private val onFalse: Command?

    /**
     * Stores the command chosen by condition.
     */
    private var chosenCommand: Command? = null

    /**
     * Creates a new ConditionalCommand with given onTrue and onFalse Commands. onFalse can be left
     * unspecified if you do not want it to be a no-op on the false condition.
     *
     * @param onTrue the command to run when the [condition] function returns true
     * @param onFalse the command to run when the [condition] function returns true
     */
    constructor(onTrue: Command, onFalse: Command?=null) {
        this.onTrue = onTrue
        this.onFalse = onFalse
    }

    private fun requireAll() {
        val onTrueRequirements = onTrue.getRequirements()
        while (onTrueRequirements.hasMoreElements()) {
            requires(onTrueRequirements.nextElement())
        }

        if (onFalse != null) {
            val onFalseRequirements = onFalse.getRequirements()
            while (onFalseRequirements.hasMoreElements()) {
                requires(onFalseRequirements.nextElement())
            }
        }
    }

    /**
     * The Condition to test to determine which [Command] to run.
     *
     * @return true if onTrue should be run or false if onFalse should be run
     */
    protected abstract fun condition(): Boolean

    override fun initializeShadowMethod() {
        chosenCommand = when (condition()) {
            true -> this.onTrue
            false -> this.onFalse
        }

        if (chosenCommand != null) {
            /*
             * This is a hack to make cancelling the chosen command inside a
             * CommandGroup work properly
             */
            chosenCommand!!.clearRequirements()

            chosenCommand!!.start()
        }
        super.initialize()
    }

    override fun cancelShadowMethod() {
        if (chosenCommand?.isRunning() == true) {
            chosenCommand!!.cancel()
        }

        super.cancelShadowMethod()
    }

    override fun isFinished(): Boolean {
        return if (chosenCommand != null) chosenCommand!!.isCompleted() else true
    }

    override fun interruptedShadowMethod() {
        if (chosenCommand?.isRunning() == true) {
            chosenCommand!!.cancel()
        }

        super.interrupted()
    }
}