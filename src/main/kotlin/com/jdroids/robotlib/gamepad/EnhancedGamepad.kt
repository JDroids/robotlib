package com.jdroids.robotlib.gamepad

import android.view.KeyEvent
import android.view.MotionEvent
import com.qualcomm.robotcore.hardware.Gamepad
import com.jdroids.robotlib.command.Command
import com.jdroids.robotlib.command.Scheduler

class EnhancedGamepad(private val gamepad: Gamepad) {
    init {
        Scheduler.registerGamepad(this)
    }

    /**
     * An enum that contains all of the boolean buttons on a gamepad
     */
    enum class Buttons {
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        A,
        B,
        X,
        Y,
        START,
        BACK,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        LEFT_JOYSTICK,
        RIGHT_JOYSTICK
    }

    /**
     * The side of a gamepad something is on
     */
    enum class Hand {LEFT, RIGHT}

    /**
     * An enum that depicts direction of a joystick
     */
    enum class Direction {X, Y}

    private val buttonToggles = hashMapOf(
        Buttons.DPAD_UP to Toggle(),
        Buttons.DPAD_DOWN to Toggle(),
        Buttons.DPAD_LEFT to Toggle(),
        Buttons.DPAD_RIGHT to Toggle(),
        Buttons.A to Toggle(),
        Buttons.B to Toggle(),
        Buttons.X to Toggle(),
        Buttons.Y to Toggle(),
        Buttons.START to Toggle(),
        Buttons.BACK to Toggle(),
        Buttons.LEFT_BUMPER to Toggle(),
        Buttons.RIGHT_BUMPER to Toggle(),
        Buttons.LEFT_JOYSTICK to Toggle(),
        Buttons.RIGHT_JOYSTICK to Toggle()
    )

    private val buttonDebouncers = hashMapOf<Buttons,Debouncer>(
        Buttons.DPAD_UP to Debouncer(),
        Buttons.DPAD_DOWN to Debouncer(),
        Buttons.DPAD_LEFT to Debouncer(),
        Buttons.DPAD_RIGHT to Debouncer(),
        Buttons.A to Debouncer(),
        Buttons.B to Debouncer(),
        Buttons.X to Debouncer(),
        Buttons.Y to Debouncer(),
        Buttons.START to Debouncer(),
        Buttons.BACK to Debouncer(),
        Buttons.LEFT_BUMPER to Debouncer(),
        Buttons.RIGHT_BUMPER to Debouncer(),
        Buttons.LEFT_JOYSTICK to Debouncer(),
        Buttons.RIGHT_JOYSTICK to Debouncer()
    )

    private fun getRawButtonValue(button: Buttons): Boolean = when (button) {
        Buttons.DPAD_UP -> gamepad.dpad_up
        Buttons.DPAD_DOWN -> gamepad.dpad_down
        Buttons.DPAD_LEFT -> gamepad.dpad_left
        Buttons.DPAD_RIGHT -> gamepad.dpad_right
        Buttons.A -> gamepad.a
        Buttons.B -> gamepad.b
        Buttons.X -> gamepad.x
        Buttons.Y -> gamepad.y
        Buttons.START -> gamepad.start
        Buttons.BACK -> gamepad.back
        Buttons.LEFT_BUMPER -> gamepad.left_bumper
        Buttons.RIGHT_BUMPER -> gamepad.right_bumper
        Buttons.LEFT_JOYSTICK -> gamepad.left_stick_button
        Buttons.RIGHT_JOYSTICK -> gamepad.right_stick_button
    }

    enum class ActivationOptions {
        TOGGLE,
        ON_PRESS
    }

    fun setCommandForButton(button: Buttons, command: Command,
                           activationOption: ActivationOptions) {
        when (activationOption) {
            ActivationOptions.TOGGLE -> buttonToggleCommands[button] = command
            ActivationOptions.ON_PRESS -> buttonPressCommands[button] = command
        }
    }

    private val buttonPressCommands = hashMapOf<Buttons, Command?>(
        Buttons.DPAD_UP to null,
        Buttons.DPAD_DOWN to null,
        Buttons.DPAD_LEFT to null,
        Buttons.DPAD_RIGHT to null,
        Buttons.A to null,
        Buttons.B to null,
        Buttons.X to null,
        Buttons.Y to null,
        Buttons.START to null,
        Buttons.BACK to null,
        Buttons.LEFT_BUMPER to null,
        Buttons.RIGHT_BUMPER to null,
        Buttons.LEFT_JOYSTICK to null,
        Buttons.RIGHT_JOYSTICK to null
    )

    private val buttonToggleCommands = hashMapOf<Buttons, Command?>(
            Buttons.DPAD_UP to null,
            Buttons.DPAD_DOWN to null,
            Buttons.DPAD_LEFT to null,
            Buttons.DPAD_RIGHT to null,
            Buttons.A to null,
            Buttons.B to null,
            Buttons.X to null,
            Buttons.Y to null,
            Buttons.START to null,
            Buttons.BACK to null,
            Buttons.LEFT_BUMPER to null,
            Buttons.RIGHT_BUMPER to null,
            Buttons.LEFT_JOYSTICK to null,
            Buttons.RIGHT_JOYSTICK to null
    )

    /**
     * Returns the current value of a given button
     *
     * @param button the button to query
     * @return the current state of the button
     */
    fun getButtonValue(button: Buttons, activationOption: ActivationOptions): Boolean {
        return when (activationOption) {
            ActivationOptions.TOGGLE -> buttonToggles[button]!!.updateToggle(getButtonValue(button,
                    ActivationOptions.ON_PRESS))
            ActivationOptions.ON_PRESS -> buttonDebouncers[button]!!.get(getRawButtonValue(button))
        }
    }

    fun getJoystick(hand: Hand, direction: Direction, deadband: Double = 0.02): Float {
        val value = when (hand) {
            Hand.LEFT -> if(direction == Direction.X) gamepad.left_stick_x else gamepad.left_stick_y
            Hand.RIGHT -> if(direction == Direction.X) gamepad.right_stick_x else
                gamepad.right_stick_y
        }

        return if (value < deadband) 0.0f else value
    }

    fun getTrigger(hand: Hand): Float {
        return when (hand) {
            Hand.LEFT -> gamepad.left_trigger
            Hand.RIGHT -> gamepad.right_trigger
        }
    }

    internal fun updateValues() {
        for ((button, toggle) in buttonToggles) {
            toggle.updateToggle(buttonDebouncers[button]!!.get(getRawButtonValue(button)))

            val pressCommand = buttonPressCommands[button]
            if (pressCommand != null) {
                if (getButtonValue(button, ActivationOptions.ON_PRESS)) {
                    Scheduler.add(pressCommand)
                }
                else {
                    Scheduler.remove(pressCommand)
                }
            }

            val toggleCommand = buttonToggleCommands[button]
            if (toggleCommand != null) {
                if (getButtonValue(button, ActivationOptions.TOGGLE)) {
                    Scheduler.add(toggleCommand)
                }
                else {
                    Scheduler.remove(toggleCommand)
                }
            }
        }
    }
}
