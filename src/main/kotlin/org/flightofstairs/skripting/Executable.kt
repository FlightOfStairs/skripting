package org.flightofstairs.skripting

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

val rootContext = ExecutionContext(System.`in`, System.out, System.err, File("").absoluteFile)

data class ExecutionContext(val stdIn: InputStream, val stdOut: OutputStream, val stdErr: OutputStream, val cwd: File)

fun <T> simpleExecutable(function: (ExecutionContext) -> T) = object : Executable<T> {
    override fun invokeWthContext(context: ExecutionContext) = function(context)
}

interface Executable<T> {
    fun invokeWthContext(context: ExecutionContext): T

    fun simpleInvoke(input: String) = simpleInvoke(input.toByteArray())

    fun simpleInvoke(input: ByteArray = ByteArray(0)): String {
        val stdOut = ByteArrayOutputStream()

        invokeWthContext(rootContext.copy(stdIn = ByteArrayInputStream(input), stdOut = stdOut))

        return stdOut.toString()
    }
}
