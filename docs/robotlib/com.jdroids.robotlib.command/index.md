[robotlib](../index.md) / [com.jdroids.robotlib.command](./index.md)

## Package com.jdroids.robotlib.command

RobotLib's command system is really the heart of the entire library. It is an effective way of representing robot code in a readable fashion.

### Types

| Name | Summary |
|---|---|
| [Command](-command/index.md) | `interface Command`<br>The [Command](-command/index.md) interface is the center of the command-based design pattern. Each command represents a certain action. |
| [CommandBuilder](-command-builder/index.md) | `class CommandBuilder`<br>[CommandBuilder](-command-builder/index.md) is a basic factory for commands from given lambdas and variables. It is not intended for actual use, but instead as a backend for [command](command.md). This enables a very clean syntax for commands. |
| [ParallelCommandGroup](-parallel-command-group/index.md) | `class ParallelCommandGroup : `[`Command`](-command/index.md)<br>A [ParallelCommandGroup](-parallel-command-group/index.md) is intended to run multiple commands at the same time. |
| [Scheduler](-scheduler/index.md) | `interface Scheduler`<br>The [Scheduler](-scheduler/index.md) interface represents what a scheduler should do/ |
| [SchedulerImpl](-scheduler-impl/index.md) | `object SchedulerImpl : `[`Scheduler`](-scheduler/index.md)<br>A fairly simple implementation of [Scheduler](-scheduler/index.md). |
| [SequentialCommandGroup](-sequential-command-group/index.md) | `class SequentialCommandGroup : `[`Command`](-command/index.md)<br>A [SequentialCommandGroup](-sequential-command-group/index.md) is intended to run multiple commands one at the time. |
| [Subsystem](-subsystem/index.md) | `interface Subsystem`<br>The [Subsystem](-subsystem/index.md) interface represents a physical component of a robot. These can be a drivetrain, a claw, etc. They should be [registered](-scheduler/register.md) at some point. |

### Annotations

| Name | Summary |
|---|---|
| [CommandDsl](-command-dsl/index.md) | `annotation class CommandDsl` |

### Functions

| Name | Summary |
|---|---|
| [command](command.md) | `fun command(block: `[`CommandBuilder`](-command-builder/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Command`](-command/index.md)<br>This function is supposed to produce a clean syntax for simple commands. |
