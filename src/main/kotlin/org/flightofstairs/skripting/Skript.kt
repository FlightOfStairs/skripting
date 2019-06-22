package org.flightofstairs.skripting

import org.flightofstairs.skripting.utils.ExecutableListBuilder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

fun skript(executionContext: ExecutionContext = rootContext, block: SkriptExecutor.() -> Any): Any =
    SkriptExecutor(executionContext).run(block)

interface Executor {
    operator fun <T> Executable<T>.invoke(): String

    operator fun String.invoke(vararg args: Arg) = LocalCommand(this)(*args)
    operator fun LocalCommand.invoke(vararg args: Arg) = CommandWithArgs(this, args.asList())()
}

class SkriptExecutor internal constructor(private val context: ExecutionContext) : Executor {
    override operator fun <T> Executable<T>.invoke(): String {
        val stdOut = ByteArrayOutputStream()

        invokeWthContext(context.copy(stdOut = stdOut))

        return stdOut.toString()
    }

    fun chain(init: ExecutableListBuilder.() -> Unit): String = Chain(ExecutableListBuilder().apply(init).getFinalList()).invoke()
    fun inDirectory(directory: File, block: Executor.() -> Any): Any = skript(context.copy(cwd = directory), block)
    fun inDirectory(directory: String, block: Executor.() -> Any) = inDirectory(File(directory), block)

    fun pipeIn(string: String, block: SingleExecutableExecutor.() -> Any): Any {
        val delegate = SkriptExecutor(context.copy(stdIn = ByteArrayInputStream(string.toByteArray())))
        return SingleExecutableExecutor(delegate).run(block)
    }
}

class SingleExecutableExecutor internal constructor(private val delegate: SkriptExecutor) : Executor {
    var invoked = false

    override fun <T> Executable<T>.invoke(): String {
        check(!invoked) { "Cannot pipe input into multiple commands" }
        invoked = true

        delegate.run {
            return invoke()
        }
    }
}
