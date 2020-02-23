
package com.example.png

import kotlinx.cinterop.*
import kotlinx.cli.*
import okio.Buffer
import okio.IOException
import platform.posix.*

@ExperimentalUnsignedTypes
fun readFrom(inputFileName: String): Buffer {
    val okioBuffer = Buffer()
    val file = fopen(inputFileName, "rb") ?: throw IOException("cannot open input file $inputFileName")
    return try {
        memScoped {
            val bufferSize = 128uL
            val buffer = allocArray<ByteVar>(bufferSize.toInt())
            do {
                val bytesRead = fread(buffer, 1, bufferSize, file)
                okioBuffer.write(buffer.readBytes(bytesRead.toInt()))
            } while(bytesRead == bufferSize)
            okioBuffer
        }
    } finally {
        fclose(file)
    }
}

// Tested via:
// ./gradlew assemble
// ./build/bin/macos/parsePngReleaseExecutable/parsePng.kexe -i ~/Pictures/white.png
// output: Result(chunks=[Ihdr(width=1, height=1, bitDepth=1, colorType=0, compressionMethod=0, filterMethod=0, interlaceMethod=0)...
fun main(args: Array<String>) {
    val parser = ArgParser("pngparser")
    val input by parser.option(ArgType.String, shortName = "i", description = "Input file").required()

    parser.parse(args)
    val inputData = readFrom(input)
    println(Parser().parse(inputData))
}