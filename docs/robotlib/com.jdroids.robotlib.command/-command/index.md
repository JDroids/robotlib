[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [Command](./index.md)

# Command

`interface Command`

The [Command](./index.md) interface is the center of the command-based design pattern.
Each command represents a certain action.

### Functions

| Name | Summary |
|---|---|
| [end](end.md) | `abstract fun end(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This is called once by [Scheduler.periodic](../-scheduler/periodic.md) when [isCompleted](is-completed.md) returns true. This typically stops motors moving, resets servos, etc. |
| [interrupt](interrupt.md) | `abstract fun interrupt(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This is called if another command with overlapping requirements is called and if [isInterruptible](is-interruptible.md) returns true. It should stop everything, but a lot of the time it just has to call [end](end.md). |
| [isCompleted](is-completed.md) | `abstract fun isCompleted(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This function tells the [Scheduler](../-scheduler/index.md) if the command is completed. |
| [isInterruptible](is-interruptible.md) | `abstract fun isInterruptible(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This function tells the [Scheduler](../-scheduler/index.md) if it should interrupt the command or throw an error if a second command with overlapping requirements is run. |
| [periodic](periodic.md) | `abstract fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This function is called by [Scheduler.periodic](../-scheduler/periodic.md) until [isCompleted](is-completed.md) returns true. This typically updates motor positions, servo positions, did logic, etc. |
| [start](start.md) | `abstract fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This function is called once whenever [Scheduler.run](../-scheduler/run.md) is called on a command. This typically initializes things for the command. |

### Inheritors

| Name | Summary |
|---|---|
| [ParallelCommandGroup](../-parallel-command-group/index.md) | `class ParallelCommandGroup : `[`Command`](./index.md)<br>A [ParallelCommandGroup](../-parallel-command-group/index.md) is intended to run multiple commands at the same time. |
| [SequentialCommandGroup](../-sequential-command-group/index.md) | `class SequentialCommandGroup : `[`Command`](./index.md)<br>A [SequentialCommandGroup](../-sequential-command-group/index.md) is intended to run multiple commands one at the time. |
