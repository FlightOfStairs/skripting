package org.flightofstairs.skripting

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object Local {
    operator fun get(path: String) = LocalCommand(path)
}

typealias Arg = String

class LocalCommand(val file: File) {
    constructor(path: String) : this(resolvePath(path))

    init {
        check(file.exists())
    }

    fun withArgs(args: List<Arg>) = BoundCommand(this, args)
    fun withArgs(vararg args: Arg) = BoundCommand(this, args.asList())

    operator fun get(a: Arg) = withArgs(a)
    operator fun get(a: Arg, b: Arg) = withArgs(a, b)
    operator fun get(a: Arg, b: Arg, c: Arg) = withArgs(a, b, c)
    operator fun get(a: Arg, b: Arg, c: Arg, d: Arg) = withArgs(a, b, c, d)
    operator fun get(a: Arg, b: Arg, c: Arg, d: Arg, e: Arg) = withArgs(a, b, c, d, e)

    operator fun invoke() = withArgs()()
    operator fun invoke(a: Arg) = withArgs(a)()
    operator fun invoke(a: Arg, b: Arg) = withArgs(a, b)()
    operator fun invoke(a: Arg, b: Arg, c: Arg) = withArgs(a, b, c)()
    operator fun invoke(a: Arg, b: Arg, c: Arg, d: Arg) = withArgs(a, b, c, d)()
    operator fun invoke(a: Arg, b: Arg, c: Arg, d: Arg, e: Arg) = withArgs(a, b, c, d, e)()
}

class BoundCommand(
    val command: LocalCommand,
    val args: List<String>,
    private val stdIn: InputStream? = null,
    private val stdOut: OutputStream? = null,
    private val stdErr: OutputStream? = null
) {

    operator fun invoke(): Int {
        val commandLine = CommandLine(command.file).apply {
            for (arg in args) {
                addArgument(arg, false)
            }
        }

        val executor = DefaultExecutor().apply {
            streamHandler = PumpStreamHandler(stdOut ?: System.out, stdErr ?: System.err, stdIn)
            setExitValues(null)
        }

        return executor.execute(commandLine)
    }
}

private val systemPath = System.getenv("PATH").split(':')

private fun resolvePath(path: String, searchPath: List<String> = systemPath): File {
    if (path.contains("/")) return File(path).absoluteFile

    return searchPath.asSequence()
            .map { File(it, path).absoluteFile }
            .find { it.exists() && it.canExecute() }
        ?: throw CommandNotFoundException(path)
}

class CommandNotFoundException(path: String) : Exception("Command '$path' not found.")
