[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Scheduler](./index.md)

# Scheduler

`interface Scheduler`

The [Scheduler](./index.md) interface represents what a scheduler should do/

### Functions

| Name | Summary |
|---|---|
| [clearSubsystemRequirements](clear-subsystem-requirements.md) | `abstract fun clearSubsystemRequirements(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This clears the subsystem requirements for a given command. |
| [periodic](periodic.md) | `abstract fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This function updates the state of everything the [Scheduler](./index.md) is keeping track of. It should be called as often as possible. |
| [register](register.md) | `abstract fun register(subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Should make it so that the given [subsystem's](../-subsystem/index.md) [periodic](../-subsystem/periodic.md) function is called when [periodic](periodic.md) is called. |
| [requires](requires.md) | `abstract fun requires(command: `[`Command`](../-command/index.md)`, subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This should ensure that a given [Command](../-command/index.md) can take control of a given [Subsystem](../-subsystem/index.md). |
| [run](run.md) | `abstract fun run(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Should start a given [Command](../-command/index.md), and add make it so that it's state is updated when [periodic](periodic.md) is called. |

### Inheritors

| Name | Summary |
|---|---|
| [SchedulerImpl](../-scheduler-impl/index.md) | `object SchedulerImpl : `[`Scheduler`](./index.md)<br>A fairly simple implementation of [Scheduler](./index.md). |
