[robotlib](../../index.md) / [com.jdroids.robotlib.controller](../index.md) / [Controller](./index.md)

# Controller

`interface Controller`

The [Controller](./index.md) interface represents a basic control loop.

### Properties

| Name | Summary |
|---|---|
| [input](input.md) | `abstract val input: () -> `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This is a lambda that should return the current state of the thing being controlled. |
| [output](output.md) | `abstract val output: (`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This is a lambda that should set the state of the thing being controlled. |
| [setpoint](setpoint.md) | `abstract val setpoint: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This is the target for the [Controller](./index.md). |

### Functions

| Name | Summary |
|---|---|
| [reset](reset.md) | `abstract fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This should reset of all the things that can possibly be reset within the controller. |
| [result](result.md) | `abstract fun result(): `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This calculates the current result of the [Controller](./index.md). |

### Inheritors

| Name | Summary |
|---|---|
| [PIDController](../-p-i-d-controller/index.md) | `interface PIDController : `[`Controller`](./index.md)<br>The [PIDController](../-p-i-d-controller/index.md) represents a simple [pid controller](https://en.wikipedia.org/wiki/PID_controller). |
