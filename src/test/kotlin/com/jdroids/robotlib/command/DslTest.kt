package com.jdroids.robotlib.command

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class DslTest {
    @Test
    fun `interruptible gets passes through`() {
        val command0 = command {
            interruptible = true
        }

        assert(command0.isInterruptible())
    }

    @Test
    fun `subsystem requirement passes through`() {
        val subsystem0 = mockk<Subsystem>()

        every {subsystem0.periodic()} just runs

        command {
            subsystem = subsystem0
        }

        assertThrows(IllegalStateException::class.java) {
            command {
                subsystem = subsystem0
            }
        }
    }

    @Test
    fun `start passes through`() {
        var hasBeenCalled = false

        command {
            start {
               hasBeenCalled = true
            }
        }.start()

        assert(hasBeenCalled)
    }

    @Test
    fun `periodic passes through`() {
        var hasBeenCalled = false

        command {
            periodic {
                hasBeenCalled = true
            }
        }.periodic()

        assert(hasBeenCalled)
    }

    @Test
    fun `end passes through`() {
        var hasBeenCalled = false

        command {
            end {
                hasBeenCalled = true
            }
        }.end()

        assert(hasBeenCalled)
    }

    @Test
    fun `interrupt passes through`() {
        var hasBeenCalled = false

        command {
            interrupt {
                hasBeenCalled = true
            }
        }.interrupt()

        assert(hasBeenCalled)
    }

    @Test
    fun `isCompleted passes through`() {
        assert(
            command {
                isCompleted { true }
            }.isCompleted()
        )
    }

    @Test
    fun `default behavior is correct`() {
        val command0 = command {  }

        assert(command0.isCompleted())

        assert(!command0.isInterruptible())
    }
}