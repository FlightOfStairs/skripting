package org.flightofstairs.skripting.utils

import org.flightofstairs.skripting.Arg
import org.flightofstairs.skripting.Chain
import org.flightofstairs.skripting.CommandWithArgs
import org.flightofstairs.skripting.Executable
import org.flightofstairs.skripting.InDirectory
import org.flightofstairs.skripting.LocalCommand
import java.io.File

class ExecutableListBuilder internal constructor() {
    private val commands = mutableListOf<Executable<out Any>>()
    private var finalized = false

    operator fun String.invoke(vararg args: Arg) = LocalCommand(this)(*args)
    operator fun LocalCommand.invoke(vararg args: Arg) = CommandWithArgs(this, args.asList())()

    fun chain(init: ExecutableListBuilder.() -> Unit) = Chain(ExecutableListBuilder().apply(init).getFinalList())
    fun inDirectory(directory: File, init: ExecutableListBuilder.() -> Unit) =
        InDirectory(directory, ExecutableListBuilder().apply(init).getFinalList())()

    operator fun Executable<out Any>.invoke() {
        check(!finalized)
        commands.add(this)
    }

    fun getFinalList(): List<Executable<out Any>> {
        check(!finalized)
        finalized = true
        return commands
    }
}
