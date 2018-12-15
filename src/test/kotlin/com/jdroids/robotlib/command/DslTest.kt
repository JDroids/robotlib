package com.jdroids.robotlib.command

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class DslTest {
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
    }
}