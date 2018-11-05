[robotlib](../../index.md) / [com.jdroids.robotlib.controller](../index.md) / [PIDControllerImpl](./index.md)

# PIDControllerImpl

`class PIDControllerImpl : `[`PIDController`](../-p-i-d-controller/index.md)

This is a simple implementation of [PIDController](../-p-i-d-controller/index.md).

### Parameters

`input` - a lambda providing the current state of the system

`output` - a lambda to do something with the given output

`setpoint` - the setpoint that the controller is trying to get to

`p` - the p coefficient

`i` - the i coefficient

`d` - the d coefficient

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PIDControllerImpl(input: () -> `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, output: (`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, setpoint: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, p: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, i: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, d: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`)`<br>This is a simple implementation of [PIDController](../-p-i-d-controller/index.md). |

### Properties

| Name | Summary |
|---|---|
| [d](d.md) | `var d: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>the d coefficient |
| [i](i.md) | `var i: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>the i coefficient |
| [input](input.md) | `var input: () -> `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>a lambda providing the current state of the system |
| [output](output.md) | `var output: (`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>a lambda to do something with the given output |
| [p](p.md) | `var p: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>the p coefficient |
| [setpoint](setpoint.md) | `var setpoint: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>the setpoint that the controller is trying to get to |

### Functions

| Name | Summary |
|---|---|
| [reset](reset.md) | `fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Resets the error sum and previous error values. |
| [result](result.md) | `fun result(): `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>Calculates the result of the controller, calls [output](output.md) with it, and then returns it. |
