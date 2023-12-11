package com.takuchan.milkypublisher.preference

import android.util.Log
import com.google.mlkit.vision.pose.Pose
import java.net.InetAddress
import java.net.DatagramSocket
import java.net.DatagramPacket

class UDPController {
    var ip = InetAddress.getByAddress(byteArrayOf(172.toByte(), 18.toByte(), 15.toByte(), 151.toByte()))
    var port = 4000

    fun receive(): String{
        val socket = DatagramSocket(port)

        val buffer = ByteArray(8192)
        val packet = DatagramPacket(buffer, buffer.size)

        socket.receive(packet)
        socket.close()

        return String(buffer)
    }


    fun send(data: Pose){
        val socket = DatagramSocket()
        val buffer = data.toString().toByteArray()
        for (item in data.allPoseLandmarks){
            val landmarkName = item.landmarkType
            val landmarkPoint = item.position
            val sendData:String = "$landmarkPoint%$landmarkName";
            val packet = DatagramPacket(sendData.toByteArray(), sendData.toByteArray().size, ip, port)
            socket.send(packet)

        }

        socket.close()
    }
}