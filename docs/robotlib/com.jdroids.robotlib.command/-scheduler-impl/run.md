[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [SchedulerImpl](index.md) / [run](./run.md)

# run

`fun run(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Overrides [Scheduler.run](../-scheduler/run.md)

[Starts](../-command/start.md) a given [Command](../-command/index.md), and adds it to a queue so that
it's [periodic](../-command/periodic.md) function is called when [periodic](periodic.md) is
called.

### Parameters

`command` - the command to run