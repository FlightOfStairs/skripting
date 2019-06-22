package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import org.flightofstairs.skripting.utils.ExecutableListBuilder

class ChainTest : StringSpec({

    "constructs chain with useful toString()" {
        val chain = Chain(ExecutableListBuilder().apply {
            "ls"()
            "grep"("-i", "foo")
            "wc"("-l")
        }.getFinalList())

        chain.toString() shouldBe "ls | grep -i foo | wc -l"
    }

    "chain runs with single command" {
        val chain = Chain(ExecutableListBuilder().apply { "echo"("hello world") }.getFinalList())

        chain.simpleInvoke().trim() shouldBe "hello world"
    }

    "chain runs with multiple commands" {
        withTempDir { dir ->
            listOf("1.txt", "2.txt", "3.png").forEach {
                dir.resolve(it).createNewFile()
            }

            val chain = Chain(ExecutableListBuilder().apply {
                "ls"(dir.absolutePath)
                "grep"("txt")
                "wc"("-l")
            }.getFinalList())

            chain.simpleInvoke().trim() shouldBe "2"
        }
    }
})
