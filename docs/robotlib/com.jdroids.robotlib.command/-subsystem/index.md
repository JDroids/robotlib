[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Subsystem](./index.md)

# Subsystem

`interface Subsystem`

The [Subsystem](./index.md) interface represents a physical component of a robot. These
can be a drivetrain, a claw, etc. They should be
[registered](../-scheduler/register.md) at some point.

### Functions

| Name | Summary |
|---|---|
| [initHardware](init-hardware.md) | `abstract fun initHardware(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This method should initialize the hardware of the component. This is typically getting all the hardware devices from hardware map. |
| [periodic](periodic.md) | `abstract fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This method is called by [Scheduler.periodic](../-scheduler/periodic.md) as long as the subsystem is registered. |
