package org.flightofstairs.skripting

import java.io.PipedInputStream
import java.io.PipedOutputStream

fun chain(init: Chain.Companion.Builder.() -> Unit) = Chain.Companion.Builder().apply(init).finalize()

class Chain(private val commands: List<Executable<out Any>>) : Executable<Any> {

    init {
        require(commands.isNotEmpty()) { "Weird looking chain." }
    }

    override fun invokeWithStreams(streams: Streams): Any {
        if (commands.isEmpty()) return Unit
        if (commands.size == 1) return commands[0].invokeWithStreams(streams)

        val (firstInput, lastOutput, commonErr) = streams

        val pipers = List(commands.size - 1) {
            val input = PipedInputStream()
            val output = PipedOutputStream(input)
            input to output
        }

        val inputs = listOf(firstInput) + pipers.map { (input, _) -> input }
        val outputs = pipers.map { (_, output) -> output } + listOf(lastOutput)

        check(commands.size == inputs.size && commands.size == outputs.size)

        return inputs.zip(outputs).zip(commands).map { (io, command) ->
            val (stdIn, stdOut) = io
            command.invokeWithStreams(Streams(stdIn, stdOut, commonErr))
        }.last()
    }

    override fun toString() = commands.joinToString(" | ")

    companion object {
        class Builder {
            private val commands = mutableListOf<Executable<out Any>>()

            operator fun String.invoke(vararg args: Arg) = LocalCommand(this)(*args)
            operator fun LocalCommand.invoke(vararg args: Arg) = commands.add(CommandWithArgs(this, args.asList()))

            fun finalize() = Chain(commands)
        }
    }
}
