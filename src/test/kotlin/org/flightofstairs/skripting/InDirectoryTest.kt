package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.flightofstairs.skripting.testUtils.withTempDir
import org.flightofstairs.skripting.utils.ExecutableListBuilder
import java.io.File

class InDirectoryTest : StringSpec({
    "Propagates directory to commands" {
        val invocationDirectories = mutableListOf<File>()

        withTempDir { dir ->
            val executables = ExecutableListBuilder().apply { simpleExecutable { invocationDirectories += it.cwd }() }
            InDirectory(dir, executables.getFinalList()).simpleInvoke()

            invocationDirectories shouldBe listOf(dir)
        }
    }
})
