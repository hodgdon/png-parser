package com.example.png

import okio.BufferedSource
import okio.ByteString
import okio.Source
import okio.buffer

@ExperimentalUnsignedTypes
class Parser {
    private val crcGenerator by lazy(LazyThreadSafetyMode.NONE) {
        CrcGenerator()
    }

    companion object {
        private const val MAGIC_0 = 0x89.toByte()
        private val MAGIC_4_TO_7 = ByteString.of(0x0D, 0x0A, 0x1A, 0x0A)
    }

    private fun parseIhdr(source: BufferedSource): Chunk.Ihdr {
        val width = source.readInt().toUInt()
        val height = source.readInt().toUInt()
        val bitDepth = source.readByte().toUByte()
        val colorType = source.readByte().toUByte()
        val compressionMethod = source.readByte().toUByte()
        val filterMethod = source.readByte().toUByte()
        val interlaceMethod = source.readByte().toUByte()
        return Chunk.Ihdr(
            width = width,
            height = height,
            bitDepth = bitDepth,
            colorType = colorType,
            compressionMethod = compressionMethod,
            filterMethod = filterMethod,
            interlaceMethod = interlaceMethod
        )
    }

    private fun parseHeader(bufferedSource: BufferedSource) {
        val magic0 = bufferedSource.readByte()
        require(magic0 == MAGIC_0) {
            "Magic number missing: expected $MAGIC_0, received $magic0"
        }
        val pngText = bufferedSource.readUtf8(3)
        require(pngText == "PNG") {
            "expected PNG, received $pngText"
        }
        val magic4To7 = bufferedSource.readByteString(4)
        require(magic4To7 == MAGIC_4_TO_7) {
            "expected $MAGIC_4_TO_7, received $magic4To7"
        }
    }

    private fun parseChunk(bufferedSource: BufferedSource): Chunk {
        val length = bufferedSource.readInt().toUInt()
        bufferedSource.require((length + 8u).toLong())
        val passedCrc = bufferedSource.peek().let { crcBuffer ->
            val computedCrc = crcGenerator.computeCrc(crcBuffer, length + 4u)
            val statedCrc = crcBuffer.readInt().toUInt()
            computedCrc == statedCrc
        }
        val name = bufferedSource.readUtf8(4)
        val chunk = if (passedCrc) {
            when (name) {
                "IHDR" -> parseIhdr(bufferedSource)
                else -> {
                    bufferedSource.skip(length.toLong())
                    Chunk.Unexpected(metadata = Chunk.Metadata(length, name), passedCrc = true)
                }
            }

        } else {
            bufferedSource.skip(length.toLong())
            Chunk.Unexpected(metadata = Chunk.Metadata(length, name), passedCrc = false)
        }
        bufferedSource.skip(4)
        return chunk
    }

    data class Result(val chunks: List<Chunk>, val terminalError: Throwable?)

    @ExperimentalUnsignedTypes
    fun parse(source: Source): Result {
        val chunks = mutableListOf<Chunk>()
        val terminalError = kotlin.runCatching {
            source.buffer().also { bufferedSource ->
                parseHeader(bufferedSource)
                while (!bufferedSource.exhausted()) {
                    chunks.add(parseChunk(bufferedSource))
                }
            }
        }.exceptionOrNull()
        return Result(chunks, terminalError)
    }
}
