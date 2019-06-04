package org.flightofstairs.skripting

fun chain(init: Chain.() -> Unit) = Chain().apply(init)

class Chain {
    val strings = mutableListOf<Pair<String, List<Arg>>>()

    operator fun String.invoke(vararg args: Arg) = strings.add(this to args.asList())
}

fun main() {
    val commands = chain {
        "ls"()
        "grep"("-i", "foo")
        "wc"("-l")
    }

    commands.strings.forEach {
        println(it)
    }
}
