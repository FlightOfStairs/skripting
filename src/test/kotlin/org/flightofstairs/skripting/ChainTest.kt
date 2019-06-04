package org.flightofstairs.skripting

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ChainTest : StringSpec({

    "constructs chain with useful toString()" {
        val chain = chain {
            "ls"()
            "grep"("-i", "foo")
            "wc"("-l")
        }

        chain.toString() shouldBe "ls | grep -i foo | wc -l"
    }
})
