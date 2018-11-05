[robotlib](../../index.md) / [com.jdroids.robotlib.controller](../index.md) / [PIDController](./index.md)

# PIDController

`interface PIDController : `[`Controller`](../-controller/index.md)

The [PIDController](./index.md) represents a simple
[pid controller](https://en.wikipedia.org/wiki/PID_controller).

### Properties

| Name | Summary |
|---|---|
| [d](d.md) | `abstract val d: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>The d coefficient of the pid controller. |
| [i](i.md) | `abstract val i: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>The i coefficient of the pid controller. |
| [p](p.md) | `abstract val p: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>The p coefficient of the pid controller. |

### Inherited Properties

| Name | Summary |
|---|---|
| [input](../-controller/input.md) | `abstract val input: () -> `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This is a lambda that should return the current state of the thing being controlled. |
| [output](../-controller/output.md) | `abstract val output: (`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This is a lambda that should set the state of the thing being controlled. |
| [setpoint](../-controller/setpoint.md) | `abstract val setpoint: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This is the target for the [Controller](../-controller/index.md). |

### Inherited Functions

| Name | Summary |
|---|---|
| [reset](../-controller/reset.md) | `abstract fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>This should reset of all the things that can possibly be reset within the controller. |
| [result](../-controller/result.md) | `abstract fun result(): `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>This calculates the current result of the [Controller](../-controller/index.md). |

### Inheritors

| Name | Summary |
|---|---|
| [PIDControllerImpl](../-p-i-d-controller-impl/index.md) | `class PIDControllerImpl : `[`PIDController`](./index.md)<br>This is a simple implementation of [PIDController](./index.md). |
