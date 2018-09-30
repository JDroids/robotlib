package com.jdroids.robotlib.command

import android.util.Log

/**
 * A [PrintCommand] is a command which prints out a string when it is initialized, and then
 * immediately finishes. It is useful if you want a [CommandGroup] to print out a string when it
 * reaches a certain point.
 */
class PrintCommand(
        /**
         * The tag to attach to the message.
         */
        private val tag: String,
        /**
         * The message to print out.
         */
        private val message: String,
        /**
         * The error level of the message.
         */
        private val errorLevel: Int = Log.INFO) : InstantCommand() {

    private val validLevels = intArrayOf(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN,
            Log.ERROR)

    override fun initialize() {
        when (errorLevel) {
            Log.VERBOSE -> Log.v(tag, message)
            Log.DEBUG -> Log.d(tag, message)
            Log.INFO -> Log.v(tag, message)
            Log.WARN -> Log.w(tag, message)
            Log.ERROR -> Log.e(tag, message)
        }
    }

    init {
        var isValidLevel = false
        for (level in validLevels) {
            if (errorLevel == level) {
                isValidLevel = true
                break
            }
        }
        if (!isValidLevel) {
            throw IllegalArgumentException("The provided errorLevel is not a valid error level. " +
                    "Given $errorLevel.")
        }
    }
}