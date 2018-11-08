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

    var interruptible = false

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

    fun interrupt(statement: () -> Unit) {
        interrupt = statement
    }

    private var interrupt: () -> Unit = {
        SchedulerImpl.clearSubsystemRequirements(builtCommand)
        end()
    }

    private lateinit var builtCommand: Command

    lateinit var subsystem: Subsystem

    private inner class BuiltCommand : Command {
        init {
            this@CommandBuilder.builtCommand = this

            if (::subsystem.isInitialized) {
                SchedulerImpl.requires(this, subsystem)
            }
        }

        override fun isInterruptible() = interruptible

        override fun isCompleted() = this@CommandBuilder.isCompleted()

        override fun start() = this@CommandBuilder.start()

        override fun periodic() = this@CommandBuilder.periodic()

        override fun end() = this@CommandBuilder.end()

        override fun interrupt() {
            this@CommandBuilder.interrupt()
        }
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