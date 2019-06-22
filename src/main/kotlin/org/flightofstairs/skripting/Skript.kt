package org.flightofstairs.skripting

import org.flightofstairs.skripting.utils.ExecutableListBuilder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

fun skript(executionContext: ExecutionContext = rootContext, block: SkriptExecutor.() -> Any): Any =
    SkriptExecutor(executionContext).run(block)

interface Executor {
    operator fun String.invoke(vararg args: Arg): String
    operator fun LocalCommand.invoke(vararg args: Arg): String
    operator fun <T> Executable<T>.invoke(): String

    fun chain(init: ExecutableListBuilder.() -> Unit): String
    fun inDirectory(directory: File, block: SkriptExecutor.() -> Any): Any
    fun inDirectory(directory: String, block: SkriptExecutor.() -> Any): Any
}

class SkriptExecutor internal constructor(private val context: ExecutionContext) : Executor {
    override operator fun String.invoke(vararg args: Arg) = LocalCommand(this)(*args)
    override operator fun LocalCommand.invoke(vararg args: Arg) = CommandWithArgs(this, args.asList())()

    override operator fun <T> Executable<T>.invoke(): String {
        val stdOut = ByteArrayOutputStream()

        invokeWthContext(context.copy(stdOut = stdOut))

        return stdOut.toString()
    }

    override fun chain(init: ExecutableListBuilder.() -> Unit): String = Chain(ExecutableListBuilder().apply(init).getFinalList()).invoke()
    override fun inDirectory(directory: File, block: SkriptExecutor.() -> Any): Any = skript(context.copy(cwd = directory), block)
    override fun inDirectory(directory: String, block: SkriptExecutor.() -> Any): Any = inDirectory(File(directory), block)

    // Todo: Make single-item only
    fun pipeIn(string: String, block: SkriptExecutor.() -> Any) =
        skript(context.copy(stdIn = ByteArrayInputStream(string.toByteArray())), block)
}
