package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SchedulerTest {
    private val subsystemMock = mockk<Subsystem>()

    @Test
    fun `periodic() calls a subsystem's periodic function`() {
        every {subsystemMock.periodic()} just runs
        SchedulerImpl.register(subsystemMock)

        SchedulerImpl.periodic()

        verify {subsystemMock.periodic()}
    }

    private val commandMock: Command = mockk()

    @Test
    fun `run() causes periodic to call a command (and end it) properly`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.end()} just runs

        every {commandMock.isCompleted()} returns false

        SchedulerImpl.run(commandMock)
        verify {commandMock.start()}

        SchedulerImpl.periodic()
        SchedulerImpl.periodic()

        verify (exactly = 2) {commandMock.periodic()}

        every {commandMock.isCompleted()} returns true

        SchedulerImpl.periodic()

        verify {commandMock.end()}

        SchedulerImpl.periodic()

        verify (exactly = 2) {commandMock.periodic()}
    }

    @Test
    fun `requires() interrupts commands when it should`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.isCompleted()} returns false
        every {commandMock.interrupt()} just runs
        every {commandMock.isInterruptible()} returns true

        SchedulerImpl.requires(commandMock, subsystemMock)

        SchedulerImpl.run(commandMock)

        val commandMock2: Command = mockk()

        every {commandMock2.start()} just runs
        every {commandMock2.periodic()} just runs
        every {commandMock2.isCompleted()} returns false
        every {commandMock2.interrupt()} just runs

        SchedulerImpl.requires(commandMock2, subsystemMock)

        SchedulerImpl.run(commandMock2)

        SchedulerImpl.periodic()

        verify {commandMock.interrupt()}
        verify (exactly = 0) {commandMock.periodic()}
        verify {commandMock2.periodic()}
    }

    @Test
    fun `requires() crashes when it should`() {
        every {commandMock.start()} just runs
        every {commandMock.periodic()} just runs
        every {commandMock.isCompleted()} returns false
        every {commandMock.interrupt()} just runs
        every {commandMock.isInterruptible()} returns false

        SchedulerImpl.requires(commandMock, subsystemMock)

        SchedulerImpl.run(commandMock)

        val commandMock2: Command = mockk()

        every {commandMock2.start()} just runs
        every {commandMock2.periodic()} just runs
        every {commandMock2.isCompleted()} returns false
        every {commandMock2.interrupt()} just runs

        assertThrows(IllegalStateException::class.java) {SchedulerImpl.requires(commandMock2, subsystemMock)}
    }
}