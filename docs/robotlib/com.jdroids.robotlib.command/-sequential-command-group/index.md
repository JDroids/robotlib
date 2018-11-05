[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [SequentialCommandGroup](./index.md)

# SequentialCommandGroup

`class SequentialCommandGroup : `[`Command`](../-command/index.md)

A [SequentialCommandGroup](./index.md) is intended to run multiple commands one at the
time.

### Parameters

`commands` - the commands to run

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SequentialCommandGroup(vararg commands: `[`Command`](../-command/index.md)`)`<br>A [SequentialCommandGroup](./index.md) is intended to run multiple commands one at the time. |

### Functions

| Name | Summary |
|---|---|
| [end](end.md) | `fun end(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [end](end.md) function of the currently running [Command](../-command/index.md). |
| [interrupt](interrupt.md) | `fun interrupt(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [interrupt](interrupt.md) function of the currently running [Command](../-command/index.md). |
| [isCompleted](is-completed.md) | `fun isCompleted(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method returns if the command group has finished running. |
| [isInterruptible](is-interruptible.md) | `fun isInterruptible(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method returns whether or not the command group should be interruptible. |
| [periodic](periodic.md) | `fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Runs the [Command.periodic](../-command/periodic.md) function of the correct [Command](../-command/index.md). |
| [start](start.md) | `fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Starts the first command. |
