package com.example.png

import okio.BufferedSource

@ExperimentalUnsignedTypes
internal class CrcGenerator {
    // https://www.w3.org/TR/PNG-CRCAppendix.html
    /* Table of CRCs of all 8-bit messages. */
    private val crcTable: List<UInt> = List(256) { n ->
        0.until(8).fold(n.toUInt()) { c: UInt, _ ->
            if (c.rem(2u) == 1u) {
                0xedb88320u.xor(c.shr(1))
            } else {
                c.shr(1)
            }
        }
    }

    private fun updateCrc(crc: UInt, source: BufferedSource, dataLength: UInt): UInt {
        return 0.until(dataLength.toLong()).fold(crc) { c, _ ->
            val byte = source.readByte().toUInt()
            val tableValue = crcTable[c.xor(byte).and(0xFFu).toInt()]
            tableValue.xor(c.shr(8))
        }
    }

    fun computeCrc(source: BufferedSource, dataLength: UInt): UInt {
        return updateCrc(UInt.MAX_VALUE, source, dataLength).xor(UInt.MAX_VALUE)
    }
}