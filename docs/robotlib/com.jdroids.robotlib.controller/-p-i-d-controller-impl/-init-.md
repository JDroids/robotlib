[robotlib](../../index.md) / [com.jdroids.robotlib.controller](../index.md) / [PIDControllerImpl](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`PIDControllerImpl(input: () -> `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, output: (`[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, setpoint: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, p: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, i: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, d: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`)`

This is a simple implementation of [PIDController](../-p-i-d-controller/index.md).

### Parameters

`input` - a lambda providing the current state of the system

`output` - a lambda to do something with the given output

`setpoint` - the setpoint that the controller is trying to get to

`p` - the p coefficient

`i` - the i coefficient

`d` - the d coefficient