[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Scheduler](index.md) / [requires](./requires.md)

# requires

`abstract fun requires(command: `[`Command`](../-command/index.md)`, subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

This should ensure that a given [Command](../-command/index.md) can take control of a given
[Subsystem](../-subsystem/index.md).

### Parameters

`command` - the [Command](../-command/index.md) to check

`subsystem` - the [Subsystem](../-subsystem/index.md) to check