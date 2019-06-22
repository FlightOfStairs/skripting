package org.flightofstairs.skripting

import java.io.PipedInputStream
import java.io.PipedOutputStream

class Chain(private val commands: List<Executable<out Any>>) : Executable<Any> {

    init {
        require(commands.isNotEmpty()) { "Weird looking chain." }
    }

    override fun invokeWthContext(context: ExecutionContext): Any {
        if (commands.isEmpty()) return Unit
        if (commands.size == 1) return commands[0].invokeWthContext(context)

        val pipers = List(commands.size - 1) {
            val input = PipedInputStream()
            val output = PipedOutputStream(input)
            input to output
        }

        val inputs = listOf(context.stdIn) + pipers.map { (input, _) -> input }
        val outputs = pipers.map { (_, output) -> output } + listOf(context.stdOut)

        check(commands.size == inputs.size && commands.size == outputs.size)

        return inputs.zip(outputs).zip(commands).map { (io, command) ->
            val (stdIn, stdOut) = io
            command.invokeWthContext(context.copy(stdIn = stdIn, stdOut = stdOut))
        }.last()
    }

    override fun toString() = commands.joinToString(" | ")
}
