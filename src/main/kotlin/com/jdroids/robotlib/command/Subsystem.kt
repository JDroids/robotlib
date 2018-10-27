package com.jdroids.robotlib.command

interface Subsystem {
    fun initHardware()

    fun periodic()
}