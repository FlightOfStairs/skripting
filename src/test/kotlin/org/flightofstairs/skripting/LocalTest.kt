package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File

class LocalTest : StringSpec({
    "Finds command on path" {
        Local["ls"].file shouldBe File("/bin/ls")
    }

    "Finds absolute commands" {
        Local["/bin/ls"].file shouldBe File("/bin/ls")
    }
})
