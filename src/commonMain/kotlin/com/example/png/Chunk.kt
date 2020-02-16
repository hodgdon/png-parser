package com.example.png

sealed class Chunk {
    @ExperimentalUnsignedTypes
    data class Unexpected(
        val metadata: Metadata,
        val passedCrc: Boolean
    ) : Chunk()

    @ExperimentalUnsignedTypes
    data class Ihdr(
        val width: UInt,
        val height: UInt,
        val bitDepth: UByte,
        val colorType: UByte,
        val compressionMethod: UByte,
        val filterMethod: UByte,
        val interlaceMethod: UByte
    ) : Chunk()

    @ExperimentalUnsignedTypes
    data class Metadata(
        val length: UInt,
        val name: String
    )
}