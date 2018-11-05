[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [SchedulerImpl](./index.md)

# SchedulerImpl

`object SchedulerImpl : `[`Scheduler`](../-scheduler/index.md)

A fairly simple implementation of [Scheduler](../-scheduler/index.md).

### Functions

| Name | Summary |
|---|---|
| [clearSubsystemRequirements](clear-subsystem-requirements.md) | `fun clearSubsystemRequirements(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This function should clear the [Subsystem](../-subsystem/index.md) requirements of a given [Command](../-command/index.md). It should be called by [periodic](periodic.md) after it [ends](../-command/end.md) a [Command](../-command/index.md). If an opmode ends for an unknown reason, it should be called by the user. |
| [periodic](periodic.md) | `fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>The [periodic](periodic.md) function updates the state of the [SchedulerImpl](./index.md). It should be called as often as possible. |
| [register](register.md) | `fun register(subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Registers a [Subsystem](../-subsystem/index.md) so that it's [periodic](../-subsystem/periodic.md) method is called when [Scheduler.periodic](../-scheduler/periodic.md) is called. |
| [requires](requires.md) | `fun requires(command: `[`Command`](../-command/index.md)`, subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Checks if a given [Command](../-command/index.md) can use a given [Subsystem](../-subsystem/index.md). |
| [run](run.md) | `fun run(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>[Starts](../-command/start.md) a given [Command](../-command/index.md), and adds it to a queue so that it's [periodic](../-command/periodic.md) function is called when [periodic](periodic.md) is called. |
