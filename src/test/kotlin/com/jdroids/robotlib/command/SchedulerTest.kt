package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class SchedulerTest {
    private val schedulerToTest = SchedulerImpl

    private val subsystemMock = mockk<Subsystem>()

    @Test
    fun `registering a subsystem causes it's periodic method to be called`() {
        val subsystemMock2 = mockk<Subsystem>()

        every {subsystemMock.periodic()} just runs
        every {subsystemMock2.periodic()} just runs

        schedulerToTest.register(subsystemMock)
        schedulerToTest.register(subsystemMock2)

        Thread.sleep(300)

        verify {subsystemMock.periodic()}
        verify {subsystemMock2.periodic()}
    }

    private val commandMock: Command = mockk()

    var isCompleted = false

    private inner class PeriodicCheckCommand : Command {
        override fun isInterruptible() = false
        override fun isCompleted() = isCompleted

        var startCalls = 0

        var periodicCalls = 0

        var endCalls = 0

        override fun start() {++startCalls}
        override fun periodic() {++periodicCalls}
        override fun end() {++endCalls}

        var isInterruptCalled = false
        override fun interrupt() {
            isInterruptCalled = true
        }
    }

    @Test
    fun `run() causes periodic to call a command (and end it) properly`() {
        val command = PeriodicCheckCommand()

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

    @Test
    fun `requires() interrupts commands when it should`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.end()} just runs
        every {commandMock.isCompleted()} returns false
        every {commandMock.interrupt()} just runs
        every {commandMock.isInterruptible()} returns true

        schedulerToTest.requires(commandMock, subsystemMock)

        schedulerToTest.run(commandMock)

        val commandMock2: Command = mockk()

        every {commandMock2.start()} just runs
        every {commandMock2.periodic()} just runs
        every {commandMock2.interrupt()} just runs
        every {commandMock2.isCompleted()} returns false

        schedulerToTest.requires(commandMock2, subsystemMock)

        schedulerToTest.run(commandMock2)

        schedulerToTest.periodic()
        schedulerToTest.periodic()

        every {commandMock.isCompleted()} returns true

        schedulerToTest.periodic()

        verify {commandMock.interrupt()}
        verify (exactly = 2) {commandMock.periodic()}
        verify {commandMock2.periodic()}
    }

    @Test
    fun `requires() crashes when it should`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.isCompleted()} returns false
        every {commandMock.interrupt()} just runs
        every {commandMock.isInterruptible()} returns false

        schedulerToTest.requires(commandMock, subsystemMock)

        schedulerToTest.run(commandMock)

        val commandMock2: Command = mockk()

        every {commandMock2.start()} just runs
        every {commandMock2.periodic()} just runs
        every {commandMock2.isCompleted()} returns false
        every {commandMock2.interrupt()} just runs

        assertThrows(IllegalStateException::class.java) {
            schedulerToTest.requires(commandMock2, subsystemMock)
        }
    }
}