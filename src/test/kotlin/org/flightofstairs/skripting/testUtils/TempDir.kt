package org.flightofstairs.skripting.testUtils

import io.kotlintest.TestContext
import java.io.File

fun <T> TestContext.withTempDir(block: (dir: File) -> T) = withTempDir(this.description().names().first(), block)

private fun <T> withTempDir(prefix: String, block: (dir: File) -> T): T {
    val dir = createTempDir(prefix = prefix)

    return block(dir).also {
        dir.deleteRecursively()
    }
}
