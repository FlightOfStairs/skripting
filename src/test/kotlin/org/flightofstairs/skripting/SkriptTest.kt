package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import java.io.File

class SkriptTest : StringSpec({
    "Runs a skript" {
        skript {
            "echo"("hello world") shouldBe "hello world\n"
        }
    }

    "Returns the last value" {
        skript { "echo"("hello world") }.trim() shouldBe "hello world"
    }

    "Runs a skript in a dir" {
        skript {
            inDirectory("/") {
                "pwd"() shouldBe "/\n"
            }
        }
    }

    "Runs a skript in dirs with nesting" {
        skript {
            inDirectory("/") {
                "pwd"() shouldBe "/\n"

                inDirectory("/var") {
                    "pwd"() shouldBe File("/var").canonicalPath + "\n"
                }
            }
        }
    }

    "Passes stdIn" {
        skript {
            pipeIn("hello world") { "cat"() } shouldBe "hello world"
        }
    }

    "Fails on piping to multiple executables" {
        skript {
            shouldThrow<IllegalStateException> {
                pipeIn("hello world") { "cat"(); "cat"() }
            }
        }
    }

    "Allows chaining commands" {
        withTempDir { dir ->
            listOf("1.txt", "2.txt", "3.png").forEach {
                dir.resolve(it).createNewFile()
            }

            skript {
                chain {
                    "ls"(dir.absolutePath)
                    "grep"("txt")
                    "wc"("-l")
                }.trim() shouldBe "2"
            }
        }
    }
})
