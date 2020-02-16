package com.example.png

import okio.Buffer
import okio.ByteString
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
internal class CrcGeneratorTest {
    fun CrcGenerator.computeCrc(byteString: ByteString) : UInt {
        return computeCrc(Buffer().write(byteString), byteString.size.toUInt())
    }

    fun assertCrc(expected : UInt, data : String) {
        val byteString = with(ByteString.Companion) {
            data.decodeHex()
        }
        assertEquals(expected, CrcGenerator().computeCrc(byteString))
    }

    @Test
    fun testComputeCrc() {
        assertCrc(930019620u, "4948445200000001000000010100000000")
        assertCrc(201089285u, "67414d410000b18f")
        assertCrc(2629456188u, "6348524d00007a26000080840000fa00000080e8000075300000ea6000003a9800001770")
        assertCrc(3716813732u, "624b47440001")
        assertCrc(3662622311u, "74494d4507e4020f160b29")
        assertCrc(3712183028u, "4944415408d76368000000820081")
        assertCrc(143963648u, "74455874646174653a63726561746500323032302d30322d31355432323a31313a34312b30303a3030")
        assertCrc(2043219644u, "74455874646174653a6d6f6469667900323032302d30322d31355432323a31313a34312b30303a3030")
        assertCrc(2923585666u, "49454e44")
    }
}