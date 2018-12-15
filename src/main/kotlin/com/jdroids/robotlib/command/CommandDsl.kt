    package com.jdroids.robotlib.command

@DslMarker
annotation class CommandDsl

/**
 * [CommandBuilder] is a basic factory for commands from given lambdas and
 * variables. It is not intended for actual use, but instead as a backend for
 * [command]. This enables a very clean syntax for commands.
 */
@CommandDsl
class CommandBuilder {
    var scheduler = SchedulerImpl

    fun isCompleted(statement: () -> Boolean) {
        isCompleted = statement
    }

    private var isCompleted: () -> Boolean = {true}

    fun start(statement: () -> Unit) {
        start = statement
    }

    private var start: () -> Unit = {}

    fun periodic(statement: () -> Unit) {
        periodic = statement
    }

    private var periodic: () -> Unit = {}

    fun end(statement: () -> Unit) {
        end = statement
    }

    private var end: () -> Unit = {}

    private lateinit var builtCommand: Command

    private inner class BuiltCommand : Command {
        init {
            this@CommandBuilder.builtCommand = this
        }

        override fun isCompleted() = this@CommandBuilder.isCompleted()

        override fun start() = this@CommandBuilder.start()

        override fun periodic() = this@CommandBuilder.periodic()

        override fun end() = this@CommandBuilder.end()
    }

    fun build(): Command = BuiltCommand()
}

/**
 * This function is supposed to produce a clean syntax for simple commands.
 *
 * @param block the block to apply
 */
fun command(block: CommandBuilder.() -> Unit) =
        CommandBuilder().apply(block).build()