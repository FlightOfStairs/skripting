package org.flightofstairs.skripting

import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.ByteArrayOutputStream

private val defaultStreams = Streams(System.`in`, System.out, System.err)

class CommandInvocationTest : StringSpec({
    "echo echos" {
        val command = CommandWithArgs(Local["echo"], listOf("hello world"))
        command().trim() shouldBe "hello world"
    }

    "cat cats" {
        val command = CommandWithArgs(Local["cat"], listOf())
        command("hello world").trim() shouldBe "hello world"
    }

    "ls errors" {
        val stdErr = ByteArrayOutputStream()

        CommandWithArgs(Local["ls"], listOf("/does-not-exist")).invokeWithStreams(defaultStreams.copy(stdErr = stdErr))
        stdErr.toString().trim() should contain("No such file or directory")
    }

    "ls does not error" {
        CommandWithArgs(Local["ls"], listOf())()
    }
})
