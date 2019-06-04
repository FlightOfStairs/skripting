package org.flightofstairs.skripting

import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File

class TempDirTest : StringSpec({

    "withTempDir cleans up after itself" {
        val dirAfterDeletion = withTempDir("TempDirTest") { dir ->
            dir.exists() shouldBe true
            dir.name shouldStartWith "TempDirTest"

            dir.resolve("foo.txt").createNewFile()

            DirWrapper(dir)
        }

        dirAfterDeletion.dir.exists() shouldBe false
    }
})

private data class DirWrapper(val dir: File)
