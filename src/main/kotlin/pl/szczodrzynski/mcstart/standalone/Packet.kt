/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

@file:Suppress("EXPERIMENTAL_API_USAGE")

package pl.szczodrzynski.mcstart.standalone

import pl.szczodrzynski.mcstart.standalone.ext.readVarInt
import pl.szczodrzynski.mcstart.standalone.ext.varLength
import pl.szczodrzynski.mcstart.standalone.ext.writeVarInt
import sun.misc.IOUtils.readNBytes
import java.io.OutputStream
import java.net.Socket

data class Packet(
    val packetId: Int,
    val length: Int,
    val data: ByteArray
) {
    companion object {
        fun readFromSocket(client: Socket): Packet {
            var length = client.inputStream.readVarInt()
            val packetId = client.inputStream.readVarInt()
            length -= varLength(packetId)
            return Packet(
                packetId,
                length,
                readNBytes(client.inputStream, length)
            )
        }

        fun withData(packetId: Int, data: List<Byte>): Packet {
            return Packet(
                packetId,
                data.size,
                data.toByteArray()
            )
        }
    }

    fun write(outputStream: OutputStream) {
        outputStream.writeVarInt(varLength(packetId) + length)
        outputStream.writeVarInt(packetId)
        outputStream.write(data)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Packet

        if (packetId != other.packetId) return false
        if (length != other.length) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = packetId
        result = 31 * result + length
        result = 31 * result + data.contentHashCode()
        return result
    }
}
