package org.flightofstairs.skripting

import java.io.File

fun <T> withTempDir(prefix: String, block: (dir: File) -> T): T {
    val dir = createTempDir(prefix = prefix)

    return block(dir).also {
        dir.deleteRecursively()
    }
}
