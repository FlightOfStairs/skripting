package org.flightofstairs.skripting.utils

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.UnspecifiedSuccess
import org.flightofstairs.skripting.simpleExecutable

class ExecutableListBuilderTest : StringSpec({
    "Allows zero-element lists" {
        ExecutableListBuilder().getFinalList() shouldBe emptyList()
    }

    "Builds multi-element lists" {
        val executable1 = simpleExecutable { UnspecifiedSuccess }
        val executable2 = simpleExecutable { UnspecifiedSuccess }

        ExecutableListBuilder().apply {
            executable1()
            executable2()
        }.getFinalList() shouldBe listOf(executable1, executable2)
    }

    "Does not allow reuse" {
        val builder = ExecutableListBuilder()
        builder.getFinalList()

        shouldThrow<IllegalStateException> {
            builder.run { simpleExecutable { UnspecifiedSuccess }() }
        }

        shouldThrow<IllegalStateException> {
            builder.getFinalList()
        }
    }
})
