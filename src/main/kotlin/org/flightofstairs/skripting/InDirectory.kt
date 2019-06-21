package org.flightofstairs.skripting

import org.flightofstairs.skripting.utils.ExecutableListBuilder
import java.io.File

fun inDirectory(directory: File, init: ExecutableListBuilder.() -> Unit) =
    InDirectory(directory, ExecutableListBuilder().apply(init).getFinalList())

class InDirectory(private val directory: File, private val executables: List<Executable<out Any>>) : Executable<Any> {
    override fun invokeWthContext(context: ExecutionContext): Any {
        if (executables.isEmpty()) return UnspecifiedSuccess

        require(directory.exists())

        val newContext = context.copy(cwd = directory)

        for (executable in executables.dropLast(1)) {
            executable.invokeWthContext(newContext)
        }

        return executables.last().invokeWthContext(newContext)
    }
}
