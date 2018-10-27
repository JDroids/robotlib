package com.jdroids.robotlib.command

class SequentialCommandGroup(private vararg val commands: Command) : Command {
    private var currentIndex = 0

    override fun isInterruptible() =
            commands.all {c: Command -> c.isInterruptible()}

    override fun isCompleted() = currentIndex >= commands.size

    override fun start() = commands[0].start()

    override fun periodic() {
        if (currentIndex < commands.size) {
            val currentCommand = commands[currentIndex]

            if (currentCommand.isCompleted()) {
                currentCommand.end()
                ++currentIndex
                commands[currentIndex].start()
            }
            else {
                currentCommand.periodic()
            }
        }
    }

    override fun end() = commands[currentIndex].end()

    override fun interrupt() = commands[currentIndex].interrupt()
}