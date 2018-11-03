package com.jdroids.robotlib.controller

interface PIDController : Controller {
    val p: Double
    val i: Double
    val d: Double
}