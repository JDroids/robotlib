[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [SchedulerImpl](index.md) / [register](./register.md)

# register

`fun register(subsystem: `[`Subsystem`](../-subsystem/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Overrides [Scheduler.register](../-scheduler/register.md)

Registers a [Subsystem](../-subsystem/index.md) so that it's [periodic](../-subsystem/periodic.md)
method is called when [Scheduler.periodic](../-scheduler/periodic.md) is called.

### Parameters

`subsystem` - the subsystem to register