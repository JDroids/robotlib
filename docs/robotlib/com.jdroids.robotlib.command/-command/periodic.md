[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Command](index.md) / [periodic](./periodic.md)

# periodic

`abstract fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

This function is called by [Scheduler.periodic](../-scheduler/periodic.md) until [isCompleted](is-completed.md)
returns true. This typically updates motor positions, servo positions,
did logic, etc.

