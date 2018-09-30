package com.jdroids.robotlib.gamepad

class Toggle {
    private var toggleOn = false
    private var togglePressed = false

    @Synchronized
    fun updateToggle(value: Boolean): Boolean {
        if (value) {
            if (!togglePressed) {
                toggleOn = !toggleOn
                togglePressed = true
            }
            else {
                togglePressed = false
            }
        }
        return toggleOn
    }
}