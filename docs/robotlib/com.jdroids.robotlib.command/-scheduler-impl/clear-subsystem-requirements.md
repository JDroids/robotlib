[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [SchedulerImpl](index.md) / [clearSubsystemRequirements](./clear-subsystem-requirements.md)

# clearSubsystemRequirements

`fun clearSubsystemRequirements(command: `[`Command`](../-command/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Overrides [Scheduler.clearSubsystemRequirements](../-scheduler/clear-subsystem-requirements.md)

This function should clear the [Subsystem](../-subsystem/index.md) requirements of a given
[Command](../-command/index.md). It should be called by [periodic](periodic.md) after it [ends](../-command/end.md)
a [Command](../-command/index.md). If an opmode ends for an unknown reason, it should be called
by the user.

