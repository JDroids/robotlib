package com.jdroids.robotlib.pid

interface PIDInterface {
    fun setPID(p: Double, i: Double, d: Double, v: Double, a: Double)

    fun setPID(p: Double, i: Double, d: Double)

    fun setP(p: Double)

    fun setI(i: Double)

    fun setD(d: Double)

    fun setV(v: Double)

    fun setA(a: Double)

    fun getP(): Double

    fun getI(): Double

    fun getD(): Double

    fun getV(): Double

    fun getA(): Double

    fun setSetpoint(setpoint: Double)

    fun getSetpoint(): Double

    fun getError(): Double

    fun reset()
}