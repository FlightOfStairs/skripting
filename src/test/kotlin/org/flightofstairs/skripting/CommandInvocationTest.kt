package org.flightofstairs.skripting

import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class CommandInvocationTest : StringSpec({
    "echo echos" {
        val stdOut = ByteArrayOutputStream()

        BoundCommand(Local["echo"], listOf("hello world"), stdOut = stdOut)()
        stdOut.toString().trim() shouldBe "hello world"
    }

    "cat cats" {
        val stdIn = ByteArrayInputStream("hello world".toByteArray())
        val stdOut = ByteArrayOutputStream()

        BoundCommand(Local["cat"], listOf(), stdIn = stdIn, stdOut = stdOut)()
        stdOut.toString().trim() shouldBe "hello world"
    }

    "ls errors" {
        val stdErr = ByteArrayOutputStream()

        BoundCommand(Local["ls"], listOf("/does-not-exist"), stdErr = stdErr)()
        stdErr.toString().trim() should contain("No such file or directory")
    }

    "ls does not error" {
        BoundCommand(Local["ls"], listOf())()
    }
})
