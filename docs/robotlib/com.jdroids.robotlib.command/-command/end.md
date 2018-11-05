[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Command](index.md) / [end](./end.md)

# end

`abstract fun end(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

This is called once by [Scheduler.periodic](../-scheduler/periodic.md) when [isCompleted](is-completed.md) returns
true. This typically stops motors moving, resets servos, etc.

