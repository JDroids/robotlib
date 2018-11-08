package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CommandGroupTest {
    @Nested
    inner class ParallelCommandGroupTest {
        val commandMock0: Command = mockk()
        val commandMock1: Command = mockk()
        val commandMock2: Command = mockk()

        val commandGroup =
                ParallelCommandGroup(commandMock0, commandMock1, commandMock2)

        @Test
        fun `Calls start() functions of all commands`() {
            every {commandMock0.start()} just runs
            every {commandMock1.start()} just runs
            every {commandMock2.start()} just runs

            commandGroup.start()

            verify {commandMock0.start()}
            verify {commandMock1.start()}
            verify {commandMock2.start()}
        }

        @Test
        fun `periodic() calls functions properly`() {
            every {commandMock0.periodic()} just runs
            every {commandMock1.periodic()} just runs
            every {commandMock2.periodic()} just runs

            every {commandMock1.end()} just runs

            every {commandMock0.isCompleted()} returns false
            every {commandMock1.isCompleted()} returns false
            every {commandMock2.isCompleted()} returns false

            commandGroup.periodic()

            verify {commandMock0.periodic()}
            verify {commandMock1.periodic()}
            verify {commandMock2.periodic()}

            every {commandMock1.isCompleted()} returns true

            commandGroup.periodic()

            verify(exactly = 2) {commandMock0.periodic()}
            verify(exactly = 1) {commandMock1.periodic()}
            verify(exactly = 2) {commandMock2.periodic()}

            verify {commandMock1.end()}

            commandGroup.periodic()

            verify(exactly = 1) {commandMock1.end()}
            verify(exactly = 1) {commandMock1.periodic()}
        }

        @Test
        fun `isInterruptable() gets value properly`() {
            every {commandMock0.isInterruptible()} returns true
            every {commandMock1.isInterruptible()} returns true
            every {commandMock2.isInterruptible()} returns true

            assert(commandGroup.isInterruptible())

            every {commandMock2.isInterruptible()} returns false

            assert(!commandGroup.isInterruptible())
        }

        @Test
        fun `isCompleted() gets value properly`() {
            every {commandMock0.isCompleted()} returns true
            every {commandMock1.isCompleted()} returns true
            every {commandMock2.isCompleted()} returns true

            assert(commandGroup.isCompleted())

            every {commandMock2.isCompleted()} returns false

            assert(!commandGroup.isCompleted())
        }
    }

    @Nested
    inner class SequentialCommandGroupTest {
        val commandMock0: Command = mockk()
        val commandMock1: Command = mockk()
        val commandMock2: Command = mockk()

        val commandGroup = SequentialCommandGroup(commandMock0, commandMock1,
                commandMock2)

        @Test
        fun `start() starts the first command and only the first command`() {
            every {commandMock0.start()} just runs
            every {commandMock1.start()} just runs

            commandGroup.start()

            verify {commandMock0.start()}
            verify(exactly = 0) {commandMock1.start()}
        }

        @Test
        fun `periodic() runs the correct command`() {
            every {commandMock0.start()} just runs
            every {commandMock1.start()} just runs
            every {commandMock2.start()} just runs

            every {commandMock0.periodic()} just runs
            every {commandMock1.periodic()} just runs
            every {commandMock2.periodic()} just runs

            every {commandMock0.end()} just runs
            every {commandMock1.end()} just runs
            every {commandMock2.end()} just runs

            every {commandMock0.isCompleted()} returns false
            every {commandMock1.isCompleted()} returns false
            every {commandMock2.isCompleted()} returns false


            commandGroup.periodic()

            verify(exactly = 1) {commandMock0.periodic()}
            verify(exactly = 0) {commandMock1.periodic()}
            verify(exactly = 0) {commandMock2.periodic()}

            every {commandMock0.isCompleted()} returns true

            commandGroup.periodic()

            verify(exactly = 1) {commandMock0.end()}

            commandGroup.periodic()

            verify(exactly = 1) {commandMock1.periodic()}
        }

        @Test
        fun `isInterruptable() gets value properly`() {
            every {commandMock0.isInterruptible()} returns true
            every {commandMock1.isInterruptible()} returns true
            every {commandMock2.isInterruptible()} returns true

            assert(commandGroup.isInterruptible())

            every {commandMock2.isInterruptible()} returns false

            assert(!commandGroup.isInterruptible())
        }

        @Test
        fun `isCompleted() gets value properly`() {
            every {commandMock0.isCompleted()} returns false
            every {commandMock1.isCompleted()} returns false
            every {commandMock2.isCompleted()} returns false

            SchedulerImpl.periodic()

            assert(!commandGroup.isCompleted())

            every {commandMock0.isCompleted()} returns true
            
            SchedulerImpl.periodic()

            assert(!commandGroup.isCompleted())

            every {commandMock1.isCompleted()} returns true

            SchedulerImpl.periodic()

            assert(!commandGroup.isCompleted())
            
            every {commandMock2.isCompleted()} returns true

            SchedulerImpl.periodic()

            assert(commandGroup.isCompleted())
        }
    }
}