[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Scheduler](index.md) / [register](./register.md)

# register

`abstract fun register(subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Should make it so that the given [subsystem's](../-subsystem/index.md)
[periodic](../-subsystem/periodic.md) function is called when [periodic](periodic.md) is
called.

### Parameters

`subsystem` - the subsystem that registers