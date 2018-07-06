package com.jdroids.robotlib.util

import com.qualcomm.robotcore.util.ElapsedTime

class EnhancedElapsedTime : ElapsedTime() {
    var isPaused = false
        private set

    private var pauseTime = System.nanoTime()

    fun pause() {
        if (isPaused) {
            return
        }
        isPaused = true
        pauseTime = System.nanoTime()
    }

    fun start() {
        if (!isPaused) {
            return
        }

        val timePaused = pauseTime - nsStartTime
        nsStartTime += timePaused
    }
}