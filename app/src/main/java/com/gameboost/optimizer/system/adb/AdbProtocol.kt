package com.gameboost.optimizer.system.adb

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Standard Android Debug Bridge (ADB) protocol definitions and framing.
 * Compliant with Google AOSP ADB protocol specification.
 */
object AdbProtocol {
    const val A_CNXN = 0x4e584e43 // "CNXN"
    const val A_AUTH = 0x48545541 // "AUTH"
    const val A_OPEN = 0x4e45504f // "OPEN"
    const val A_OKAY = 0x59414b4f // "OKAY"
    const val A_CLSE = 0x45534c43 // "CLSE"
    const val A_WRTE = 0x45545257 // "WRTE"

    const val A_VERSION_MIN = 0x01000000
    const val A_VERSION_SKIP_CHECKSUM = 0x01000001
    const val MAX_PAYLOAD = 256 * 1024

    const val AUTH_TYPE_TOKEN = 1
    const val AUTH_TYPE_SIGNATURE = 2
    const val AUTH_TYPE_RSAPUBLICKEY = 3

    class AdbMessage(
        val command: Int,
        val arg0: Int,
        val arg1: Int,
        val payload: ByteArray = ByteArray(0)
    ) {
        val dataLength: Int get() = payload.size

        fun checksum(): Int {
            var sum = 0
            for (b in payload) {
                sum += (b.toInt() and 0xFF)
            }
            return sum
        }

        fun magic(): Int = command xor -1

        fun serialize(): ByteArray {
            val buf = ByteBuffer.allocate(24 + payload.size).order(ByteOrder.LITTLE_ENDIAN)
            buf.putInt(command)
            buf.putInt(arg0)
            buf.putInt(arg1)
            buf.putInt(dataLength)
            buf.putInt(checksum())
            buf.putInt(magic())
            if (payload.isNotEmpty()) {
                buf.put(payload)
            }
            return buf.array()
        }

        companion object {
            fun parseHeader(headerBytes: ByteArray): AdbHeader {
                val buf = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN)
                val cmd = buf.getInt()
                val a0 = buf.getInt()
                val a1 = buf.getInt()
                val len = buf.getInt()
                val sum = buf.getInt()
                val mag = buf.getInt()
                return AdbHeader(cmd, a0, a1, len, sum, mag)
            }
        }
    }

    data class AdbHeader(
        val command: Int,
        val arg0: Int,
        val arg1: Int,
        val dataLength: Int,
        val checksum: Int,
        val magic: Int
    ) {
        val isValid: Boolean get() = (command xor -1) == magic
    }
}
