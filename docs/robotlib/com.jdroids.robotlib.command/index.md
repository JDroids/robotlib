[robotlib](../index.md) / [com.jdroids.robotlib.command](./index.md)

## Package com.jdroids.robotlib.command

### Types

| Name | Summary |
|---|---|
| [Command](-command/index.md) | `interface Command`<br>The [Command](-command/index.md) interface is the center of the command-based design pattern. Each command represents a certain action. |
| [ParallelCommandGroup](-parallel-command-group/index.md) | `class ParallelCommandGroup : `[`Command`](-command/index.md)<br>A [ParallelCommandGroup](-parallel-command-group/index.md) is intended to run multiple commands at the same time. |
| [Scheduler](-scheduler/index.md) | `interface Scheduler`<br>The [Scheduler](-scheduler/index.md) interface represents what a scheduler should do/ |
| [SchedulerImpl](-scheduler-impl/index.md) | `object SchedulerImpl : `[`Scheduler`](-scheduler/index.md)<br>A fairly simple implementation of [Scheduler](-scheduler/index.md). |
| [SequentialCommandGroup](-sequential-command-group/index.md) | `class SequentialCommandGroup : `[`Command`](-command/index.md)<br>A [SequentialCommandGroup](-sequential-command-group/index.md) is intended to run multiple commands one at the time. |
| [Subsystem](-subsystem/index.md) | `interface Subsystem`<br>The [Subsystem](-subsystem/index.md) interface represents a physical component of a robot. These can be a drivetrain, a claw, etc. They should be [registered](-scheduler/register.md) at some point. |
