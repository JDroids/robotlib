[robotlib](../../index.md) / [com.jdroids.robotlib.command](../index.md) / [CommandBuilder](./index.md)

# CommandBuilder

`class CommandBuilder`

[CommandBuilder](./index.md) is a basic factory for commands from given lambdas and
variables. It is not intended for actual use, but instead as a backend for
[command](../command.md). This enables a very clean syntax for commands.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CommandBuilder()`<br>[CommandBuilder](./index.md) is a basic factory for commands from given lambdas and variables. It is not intended for actual use, but instead as a backend for [command](../command.md). This enables a very clean syntax for commands. |

### Properties

| Name | Summary |
|---|---|
| [interruptible](interruptible.md) | `var interruptible: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [scheduler](scheduler.md) | `var scheduler: `[`SchedulerImpl`](../-scheduler-impl/index.md) |
| [subsystem](subsystem.md) | `lateinit var subsystem: `[`Subsystem`](../-subsystem/index.md) |

### Functions

| Name | Summary |
|---|---|
| [build](build.md) | `fun build(): `[`Command`](../-command/index.md) |
| [end](end.md) | `fun end(statement: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [interrupt](interrupt.md) | `fun interrupt(statement: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [isCompleted](is-completed.md) | `fun isCompleted(statement: () -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [periodic](periodic.md) | `fun periodic(statement: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [start](start.md) | `fun start(statement: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
