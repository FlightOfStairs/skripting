package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ChainTest : StringSpec({

    "constructs chain with useful toString()" {
        val chain = chain {
            "ls"()
            "grep"("-i", "foo")
            "wc"("-l")
        }

        chain.toString() shouldBe "ls | grep -i foo | wc -l"
    }

    "chain runs with single command" {
        chain { "echo"("hello world") }().trim() shouldBe "hello world"
    }

    "chain runs with multiple commands" {
        withTempDir("ChainTest") { dir ->
            listOf("1.txt", "2.txt", "3.png").forEach {
                dir.resolve(it).createNewFile()
            }

            val testChain = chain {
                "ls"(dir.absolutePath)
                "grep"("txt")
                "wc"("-l")
            }

            testChain().trim() shouldBe "2"
        }
    }
})
