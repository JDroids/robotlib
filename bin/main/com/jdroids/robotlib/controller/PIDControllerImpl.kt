package com.jdroids.robotlib.controller

class PIDControllerImpl(override var input: () -> Double,
                        override var output: (Double) -> Unit,
                        override var setpoint: Double,
                        override var p: Double,
                        override var i: Double,
                        override var d: Double) : PIDController {

    private var lastTime = System.nanoTime() * 1_000_000_000

    private var errorSum = 0.0
    private var lastError = 0.0

    override fun result(): Double {
        val now = System.nanoTime() * 1_000_000_000
        val timeChange = lastTime - now
        lastTime = now

        val error = setpoint - input()

        errorSum += error

        val result = (p * error) +
                (i*timeChange * errorSum) +
                (d/timeChange * (error - lastError))

        lastError = error

        output(result)
        return result
    }

    override fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTime = System.nanoTime() * 1_000_000_000
    }
}