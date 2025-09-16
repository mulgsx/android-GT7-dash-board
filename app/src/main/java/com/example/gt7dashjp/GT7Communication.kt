package com.example.gt7dashjp

import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.bouncycastle.crypto.engines.Salsa20Engine
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.crypto.params.KeyParameter

import android.util.Log
import androidx.compose.ui.geometry.Offset

class GT7Communication(
    private val playstationIp: String,
    private val onPacketReceived: (Int, Float) -> Unit
) : Thread() {

    private val sendPort = 33739
    private val receivePort = 33740
    @Volatile private var shallRun = true
    private var packetCount = 0

    override fun run() {
        try {
            DatagramSocket(receivePort).use { socket ->
                socket.soTimeout = 5000
                sendHeartbeat()

                while (shallRun) {
                    try {
                        val buffer = ByteArray(4096)
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)

                        val rawData = packet.data.copyOf(packet.length)
                        val decoded = decodeSalsa20(rawData)


                        if (decoded.isNotEmpty()) {
                            val rpm = getFloat(decoded, 0x1C)
                            packetCount++
                            onPacketReceived(packetCount, rpm)

                            if (packetCount % 100 == 0) sendHeartbeat()
                        }
                        //前回の処理
//                        packetCount++
//                        onPacketReceived(packetCount)
//                        if (packetCount % 100 == 0) sendHeartbeat()



                    } catch (e: SocketTimeoutException) {
                        sendHeartbeat()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GT7Communication", "通信エラー: ${e.message}", e)
        }
    }

    fun stopCommunication() {
        shallRun = false
    }

    ///ハートビート送信関数
    private fun sendHeartbeat() {
        try {
            DatagramSocket().use { sendSocket ->
                val data = "A".toByteArray()
                val address = InetAddress.getByName(playstationIp)
                val packet = DatagramPacket(data, data.size, address, sendPort)
                sendSocket.send(packet)
            }
        } catch (e: Exception) {
            Log.e("GT7Communication", "ハートビート送信エラー: ${e.message}", e)
        }
    }

    ///復号化関数
    private fun decodeSalsa20(dat: ByteArray): ByteArray {
        try {
            val key = "Simulator Interface Packet GT7 ver 0.0".toByteArray().copyOf(32)
            val oiv = dat.copyOfRange(0x40, 0x44)
            val iv1 = ByteBuffer.wrap(oiv).order(ByteOrder.LITTLE_ENDIAN).int
            val iv2 = iv1 xor 0xDEADBEAF.toInt()

            val iv = ByteArray(8)
            ByteBuffer.wrap(iv).order(ByteOrder.LITTLE_ENDIAN).apply {
                putInt(iv2)
                putInt(iv1)
            }

            val cipher = Salsa20Engine()
            cipher.init(false, ParametersWithIV(KeyParameter(key), iv))

            val decrypted = ByteArray(dat.size)
            cipher.processBytes(dat, 0, dat.size, decrypted, 0)

            val magic = ByteBuffer.wrap(decrypted, 0, 4).order(ByteOrder.LITTLE_ENDIAN).int
            return if (magic == 0x47375330) decrypted else ByteArray(0)
        } catch (e: Exception) {
            Log.e("GT7Communication", "復号エラー: ${e.message}", e)
            return ByteArray(0)
        }
    }


    //float
    private fun getFloat(decoded: ByteArray, offset: Int): Float {
        return if (decoded.size >= offset + 4) {
            ByteBuffer.wrap(decoded, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .float
        } else 0f
    }


}

