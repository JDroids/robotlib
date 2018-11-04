package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.Period

class SchedulerTest {
    private val subsystemMock = mockk<Subsystem>()
    private val schedulerToTest = SchedulerImpl

    @Test
    fun `periodic() calls a subsystem's periodic function`() {
        every {subsystemMock.periodic()} just runs
        schedulerToTest.register(subsystemMock)

        schedulerToTest.periodic()

        verify {subsystemMock.periodic()}
    }

    private val commandMock: Command = mockk()

    var isCompleted = false

    private inner class PeriodicCheckCommand : Command {
        override fun isInterruptible() = false
        override fun isCompleted() = isCompleted

        var periodicCalls = 0

        override fun start() {}
        override fun periodic() {++periodicCalls}
        override fun end() {}

        var isInterruptCalled = false
        override fun interrupt() {
            isInterruptCalled = true
        }
    }

    @Test
    fun `run() causes periodic to call a command (and end it) properly`() {
        val command = PeriodicCheckCommand()

        schedulerToTest.run(command)
        verify {command.start()}

        schedulerToTest.periodic()
        schedulerToTest.periodic()

        val periodicAtThisTime = command.periodicCalls

        verify (exactly = periodicAtThisTime) {command.periodic()}

        isCompleted = true

        schedulerToTest.periodic()

        verify {command.end()}

        schedulerToTest.periodic()

        verify (exactly = periodicAtThisTime) {command.periodic()}
    }

    @Test
    fun `requires() interrupts commands when it should`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.isCompleted()} returns false
        every {commandMock.interrupt()} just runs
        every {commandMock.isInterruptible()} returns true

        schedulerToTest.requires(commandMock, subsystemMock)

        schedulerToTest.run(commandMock)

        val commandMock2: Command = mockk()

        every {commandMock2.start()} just runs
        every {commandMock2.periodic()} just runs
        every {commandMock2.isCompleted()} returns false
        every {commandMock2.interrupt()} just runs

        schedulerToTest.requires(commandMock2, subsystemMock)

        schedulerToTest.run(commandMock2)

        schedulerToTest.periodic()
        schedulerToTest.periodic()
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

        assertThrows(IllegalStateException::class.java) {schedulerToTest.requires(commandMock2, subsystemMock)}
    }
}