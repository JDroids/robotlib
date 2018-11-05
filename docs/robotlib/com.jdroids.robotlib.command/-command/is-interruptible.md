[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Command](index.md) / [isInterruptible](./is-interruptible.md)

# isInterruptible

`abstract fun isInterruptible(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

This function tells the [Scheduler](../-scheduler/index.md) if it should interrupt the command or
throw an error if a second command with overlapping requirements is run.

**Return**
if the command should be interrupted when a command with
overlapping requirements is run

