package com.jdroids.robotlib.gamepad

import com.qualcomm.robotcore.util.ElapsedTime

/**
 * A class that helps with a mechanical switch activating once when a button is hit once
 *
 * @param period
 *      The amount of time for which the button will ignore a second press
 */
class Debouncer(private val period: Double = 0.25) {
    private var latest = 0.0
    private val elapsedTime = ElapsedTime()

    init {
        elapsedTime.reset()
    }

    /**
     * Get the current, debounced value
     *
     * @param value the raw value
     */
    @Synchronized
    fun get(value: Boolean): Boolean {
        val now = elapsedTime.milliseconds()
        if (value) {
            if (now-latest > period) {
                latest = now
                return true
            }
        }
        return false
    }
}