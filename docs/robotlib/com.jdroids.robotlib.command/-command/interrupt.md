[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Command](index.md) / [interrupt](./interrupt.md)

# interrupt

`abstract fun interrupt(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

This is called if another command with overlapping requirements is called
and if [isInterruptible](is-interruptible.md) returns true. It should stop everything, but a
lot of the time it just has to call [end](end.md).

