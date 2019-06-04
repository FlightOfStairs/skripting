package org.flightofstairs.skripting

fun chain(init: Chain.() -> Unit) = Chain().apply(init)

class Chain {
    private val commands = mutableListOf<BoundCommand>()

    operator fun String.invoke(vararg args: Arg) = LocalCommand(this)(*args)
    operator fun LocalCommand.invoke(vararg args: Arg) = commands.add(BoundCommand(this, args.asList()))

    override fun toString() = commands.joinToString(" | ")
}

fun main() {
    val commands = chain {
        "ls"()
        "grep"("-i", "foo")
        "wc"("-l")
    }

    println(commands) // /bin/ls | /usr/bin/grep -i foo | /usr/bin/wc -l
}
