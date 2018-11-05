[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [ParallelCommandGroup](./index.md)

# ParallelCommandGroup

`class ParallelCommandGroup : `[`Command`](../-command/index.md)

A [ParallelCommandGroup](./index.md) is intended to run multiple commands at the same
time.

### Parameters

`commands` - the commands to run at the same time

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ParallelCommandGroup(vararg commands: `[`Command`](../-command/index.md)`)`<br>A [ParallelCommandGroup](./index.md) is intended to run multiple commands at the same time. |

### Functions

| Name | Summary |
|---|---|
| [end](end.md) | `fun end(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [Command.end](../-command/end.md) method of each command within the command group. |
| [interrupt](interrupt.md) | `fun interrupt(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [Command.interrupt](../-command/interrupt.md) method of each command within the command group. |
| [isCompleted](is-completed.md) | `fun isCompleted(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method returns if the command group has finished running. |
| [isInterruptible](is-interruptible.md) | `fun isInterruptible(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method returns whether or not the command group should be interruptible. |
| [periodic](periodic.md) | `fun periodic(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [Command.periodic](../-command/periodic.md) method of each command within the command group. |
| [start](start.md) | `fun start(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Calls the [Command.start](../-command/start.md) method of each command within the command group. |
