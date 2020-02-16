package com.example.png

import com.example.png.Chunk.Metadata
import okio.Buffer
import okio.ByteString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


@ExperimentalUnsignedTypes
class ParserTest {
    @Test
    fun whenPngIs1x1WhitePixel_ihrdChunkReports1x1() {
        val parsed = Parser().parse(Buffer().write(with(ByteString.Companion){
            "89504e470d0a1a0a0000000d4948445200000001000000010100000000376ef924".decodeHex()
        }))
        assertNull(parsed.terminalError)
        assertEquals(1, parsed.chunks.size)
        val firstChunk = parsed.chunks.first()
        assertTrue(firstChunk is Chunk.Ihdr)
        assertEquals(1u, firstChunk.width)
        assertEquals(1u, firstChunk.height)
    }

    @Test
    fun whenPngIs1x1WhitePixelGeneratedByImagemagick_allChunksPassCrc() {
        val parsed = Parser().parse(Buffer().write(with(ByteString) {
            """
            89504e470d0a1a0a0000000d494844520000000100000001010000000037
            6ef9240000000467414d410000b18f0bfc6105000000206348524d00007a
            26000080840000fa00000080e8000075300000ea6000003a98000017709c
            ba513c00000002624b47440001dd8a13a40000000774494d4507e4020f16
            0b29da4f2e670000000a4944415408d76368000000820081dd436af40000
            002574455874646174653a63726561746500323032302d30322d31355432
            323a31313a34312b30303a30300894b6000000002574455874646174653a
            6d6f6469667900323032302d30322d31355432323a31313a34312b30303a
            303079c90ebc0000000049454e44ae426082
        """.replace("\\s".toRegex(), "").decodeHex()
        }))
        assertNull(parsed.terminalError)
        assertEquals(9, parsed.chunks.size)
        assertEquals(
            expected = listOf(
                Metadata(length = 4u, name = "gAMA"),
                Metadata(length = 32u, name = "cHRM"),
                Metadata(length = 2u, name = "bKGD"),
                Metadata(length = 7u, name = "tIME"),
                Metadata(length = 10u, name = "IDAT"),
                Metadata(length = 37u, name = "tEXt"),
                Metadata(length = 37u, name = "tEXt"),
                Metadata(length = 0u, name = "IEND")
            ).map { metadata ->
                Chunk.Unexpected(metadata, passedCrc = true)
            },
            actual = parsed.chunks.drop(1)
        )
    }
}