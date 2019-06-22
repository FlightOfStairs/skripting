package org.flightofstairs.skripting

import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import java.io.ByteArrayOutputStream

class CommandInvocationTest : StringSpec({
    "echo echos" {
        val command = CommandWithArgs(Local("echo"), listOf("hello world"))
        command.simpleInvoke().trim() shouldBe "hello world"
    }

    "cat cats" {
        val command = CommandWithArgs(Local("cat"), listOf())
        command.simpleInvoke("hello world").trim() shouldBe "hello world"
    }

    "ls errors" {
        val stdErr = ByteArrayOutputStream()

        CommandWithArgs(Local("ls"), listOf("/does-not-exist")).invokeWthContext(rootContext.copy(stdErr = stdErr))
        stdErr.toString().trim() should contain("No such file or directory")
    }

    "ls does not error" {
        CommandWithArgs(Local("ls"), listOf()).simpleInvoke()
    }

    "Runs in given directory" {
        withTempDir { dir ->
            val stdOut = ByteArrayOutputStream()

            CommandWithArgs(LocalCommand("pwd"), listOf()).invokeWthContext(rootContext.copy(cwd = dir, stdOut = stdOut))

            stdOut.toString().trim() shouldBe dir.canonicalPath
        }
    }
})
