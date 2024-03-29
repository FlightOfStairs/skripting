package org.flightofstairs.skripting

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File

object Local {
    operator fun invoke(path: String) = LocalCommand(path)
}

typealias Arg = String

class LocalCommand(private val givenCommand: String) {
    val executable = resolveExecutable(givenCommand)

    init {
        check(executable.exists())
    }

    fun withArgs(vararg args: Arg) = CommandWithArgs(this, args.asList())

    operator fun get(a: Arg) = withArgs(a)
    operator fun get(a: Arg, b: Arg) = withArgs(a, b)
    operator fun get(a: Arg, b: Arg, c: Arg) = withArgs(a, b, c)
    operator fun get(a: Arg, b: Arg, c: Arg, d: Arg) = withArgs(a, b, c, d)
    operator fun get(a: Arg, b: Arg, c: Arg, d: Arg, e: Arg) = withArgs(a, b, c, d, e)

    override fun toString() = givenCommand.trim()
}

class CommandWithArgs(private val command: LocalCommand, private val args: List<String>) : Executable<Int> {

    override fun invokeWthContext(context: ExecutionContext): Int {
        val commandLine = CommandLine(command.executable).apply {
            for (arg in args) {
                addArgument(arg, false)
            }
        }

        val executor = DefaultExecutor().apply {
            workingDirectory = context.cwd
            streamHandler = PumpStreamHandler(context.stdOut, context.stdErr, context.stdIn)
            setExitValues(null)
        }

        return executor.execute(commandLine)
    }

    override fun toString() = "$command ${args.joinToString(" ")}".trim()
}

private val systemPath = System.getenv("PATH").split(':')

private fun resolveExecutable(path: String, searchPath: List<String> = systemPath): File {
    if (path.contains("/")) return File(path).absoluteFile

    return searchPath.asSequence()
            .map { File(it, path).absoluteFile }
            .find { it.exists() && it.canExecute() }
        ?: throw CommandNotFoundException(path)
}

class CommandNotFoundException(path: String) : Exception("Command '$path' not found.")
