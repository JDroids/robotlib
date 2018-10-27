package com.jdroids.robotlib.controller

interface Controller {
    val input: () -> Double
    val output: (Double) -> Unit

    val setpoint: Double

    fun result(): Double

    fun reset()
}