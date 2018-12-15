package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class SchedulerTest {
    private val schedulerToTest = SchedulerImpl

    private val commandMock: Command = mockk()

    var isCompleted = false

    private inner class FunctionCallCheckCommand : Command {
        override fun isCompleted() = isCompleted

        var startCalls = 0

        var periodicCalls = 0

        var endCalls = 0

        override fun start() {++startCalls}
        override fun periodic() {++periodicCalls}
        override fun end() {++endCalls}
    }

    @Test
    fun `run() causes periodic to call a command (and end it) properly`() {
        val command = FunctionCallCheckCommand()

        schedulerToTest.run(command)
        assert(command.startCalls == 1)

        schedulerToTest.periodic()
        schedulerToTest.periodic()

        val periodicAtThisTime = command.periodicCalls

        isCompleted = true

        schedulerToTest.periodic()

        assert(command.endCalls == 1)

        schedulerToTest.periodic()

        assert(command.periodicCalls == periodicAtThisTime)
    }
}