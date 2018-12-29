package com.jdroids.robotlib.command

import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CommandGroupTest {
    class MockCommand() : Command {
        data class CommandState(
            val startCount: Int,
            val periodicCount: Int,
            val endCount: Int
        )

        var state: CommandState = CommandState(0,0,0)
            private set
        
        override fun start() {
            state = state.copy(
                startCount = state.startCount + 1
            )    
        }

        override fun periodic() {
            state = state.copy(
                periodicCount = state.periodicCount + 1
            )    
        }
        
        override fun end() {
            state = state.copy(
                endCount = state.endCount + 1
            )    
        }

        var hasCompleted = false

        override fun isCompleted() : Boolean {
            return hasCompleted
        }
    }



    @Nested
    inner class ParallelCommandGroupTest {
        val command1 = MockCommand()
        val command2 = MockCommand()

        val commandGroup =
                ParallelCommandGroup(command1, command2)

        @Test
        fun `2 commands loaded, one terminates first`() {
            assertEquals(MockCommand.CommandState(0,0,0), command1.state)   
            assertEquals(MockCommand.CommandState(0,0,0), command2.state)

            SchedulerImpl.run(commandGroup)

            assertEquals(MockCommand.CommandState(1,0,0), command1.state)   
            assertEquals(MockCommand.CommandState(1,0,0), command2.state)

            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,1,0), command1.state)   
            assertEquals(MockCommand.CommandState(1,1,0), command2.state)

            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,0), command1.state)   
            assertEquals(MockCommand.CommandState(1,2,0), command2.state)

            command2.hasCompleted = true
            
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,3,0), command1.state)   
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
        
            SchedulerImpl.periodic()

            assertEquals(MockCommand.CommandState(1,4,0), command1.state)   
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
            
        }
    }

    @Nested
    inner class SequentialCommandGroupTest {
        val command1 = MockCommand()
        val command2 = MockCommand()
        val command3 = MockCommand()

        val commandGroup = SequentialCommandGroup(command1, command2, command3)

        @Test
        fun `Test SequentialCommandGroup with 3 commands`() {
            assertEquals(MockCommand.CommandState(0,0,0), command1.state)
            assertEquals(MockCommand.CommandState(0,0,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)

            SchedulerImpl.run(commandGroup)

            assertEquals(MockCommand.CommandState(1,0,0), command1.state)
            assertEquals(MockCommand.CommandState(0,0,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)

            SchedulerImpl.periodic()

            assertEquals(MockCommand.CommandState(1,1,0), command1.state)
            assertEquals(MockCommand.CommandState(0,0,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)
        
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,0), command1.state)
            assertEquals(MockCommand.CommandState(0,0,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)

            command1.hasCompleted = true
                    
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,0,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)
            
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,1,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)

            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,2,0), command2.state)
            assertEquals(MockCommand.CommandState(0,0,0), command3.state)

            command2.hasCompleted = true

            SchedulerImpl.periodic()

            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
            assertEquals(MockCommand.CommandState(1,0,0), command3.state)

            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
            assertEquals(MockCommand.CommandState(1,1,0), command3.state)
            
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
            assertEquals(MockCommand.CommandState(1,2,0), command3.state)

            command3.hasCompleted = true
                    
            SchedulerImpl.periodic()
            
            assertEquals(MockCommand.CommandState(1,2,1), command1.state)
            assertEquals(MockCommand.CommandState(1,2,1), command2.state)
            assertEquals(MockCommand.CommandState(1,2,1), command3.state)

            assert(commandGroup.isCompleted())
        }
    }
}