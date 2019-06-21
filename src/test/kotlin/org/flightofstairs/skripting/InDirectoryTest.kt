package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import java.io.File

class InDirectoryTest : StringSpec({
    "Propagates directory to commands" {
        val invocationDirectories = mutableListOf<File>()

        withTempDir { dir ->
            inDirectory(dir) {
                simpleExecutable { invocationDirectories += it.cwd }()
            }.invoke()

            invocationDirectories shouldBe listOf(dir)
        }
    }

    "Can be nested" {
        val invocationDirectories = mutableListOf<File>()

        withTempDir { outer ->
            val inner = outer.resolve("inner").apply {
                mkdir()
            }

            inDirectory(outer) {
                simpleExecutable { invocationDirectories += it.cwd }()

                inDirectory(inner) {
                    simpleExecutable { invocationDirectories += it.cwd }()
                }
            }.invoke()

            invocationDirectories shouldBe listOf(outer, inner)
        }
    }
})
