[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Scheduler](index.md) / [run](./run.md)

# run

`abstract fun run(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Should start a given [Command](../-command/index.md), and add make it so that it's state is
updated when [periodic](periodic.md) is called.

### Parameters

`command` - the command to run