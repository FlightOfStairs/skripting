package org.flightofstairs.skripting

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

data class Streams(val stdIn: InputStream, val stdOut: OutputStream, val stdErr: OutputStream)

interface Executable<T> {
    fun invokeWithStreams(streams: Streams): T

    operator fun invoke(input: String) = invoke(input.toByteArray())

    operator fun invoke(input: ByteArray = ByteArray(0)): String {
        val stdOut = ByteArrayOutputStream()

        invokeWithStreams(Streams(ByteArrayInputStream(input), stdOut, System.err))

        return stdOut.toString()
    }
}
