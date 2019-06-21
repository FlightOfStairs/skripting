package org.flightofstairs.skripting

import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import java.io.ByteArrayOutputStream

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

        CommandWithArgs(Local["ls"], listOf("/does-not-exist")).invokeWthContext(rootContext.copy(stdErr = stdErr))
        stdErr.toString().trim() should contain("No such file or directory")
    }

    "ls does not error" {
        CommandWithArgs(Local["ls"], listOf())()
    }

    "cwd is propagated" {
        withTempDir { dir ->
            val pwdOutput = inDirectory(dir.canonicalFile) {
                "pwd"()
            }.invoke().trim()
            pwdOutput shouldBe dir.canonicalPath
        }
    }
})
