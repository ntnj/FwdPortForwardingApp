/*
 * Fwd: the port forwarding app
 * Copyright (C) 2016  Elixsr Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.elixsr.portforwarder.forwarding

import android.util.Log
import com.elixsr.portforwarder.exceptions.BindException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.Callable

/**
 * Skeleton taken from: http://cs.ecs.baylor.edu/~donahoo/practical/JavaSockets2/code/UDPEchoServerSelector.java
 *
 *
 * Created by Niall McShane on 21/02/2016.
 */
class UdpForwarder(form: InetSocketAddress, to: InetSocketAddress?, ruleName: String?) : Forwarder("UDP", form, to, ruleName), Callable<Void?> {
    @Throws(IOException::class, BindException::class)
    override fun call(): Void? {
        Log.d(TAG, String.format(START_MESSAGE, protocol, from.port, to!!.port))
        try {
            val readBuffer = ByteBuffer.allocate(BUFFER_SIZE)
            val inChannel = DatagramChannel.open()
            inChannel.configureBlocking(false)
            try {
                inChannel.socket().bind(from)
            } catch (e: SocketException) {
                Log.e(TAG, String.format(BIND_FAILED_MESSAGE, from.port, protocol, ruleName), e)
                throw BindException(String.format(BIND_FAILED_MESSAGE, from.port, protocol, ruleName), e)
            }
            val selector = Selector.open()
            inChannel.register(selector, SelectionKey.OP_READ, ClientRecord(to))
            while (true) { // Run forever, receiving and echoing datagrams
                if (Thread.currentThread().isInterrupted) {
                    Log.i(TAG, String.format(THREAD_INTERRUPT_CLEANUP_MESSAGE, protocol))
                    inChannel.socket().close()
                    break
                }
                val count = selector.select()
                if (count > 0) {


                    // Get iterator on set of keys with I/O to process
                    val keyIter = selector.selectedKeys().iterator()
                    while (keyIter.hasNext()) {
                        val key = keyIter.next() // Key is bit mask

                        // Client socket channel has pending data?
                        if (key.isReadable) {
                            // Log.i(TAG, "Have Something to READ");
                            handleRead(key, readBuffer)
                        }

                        // Client socket channel is available for writing and
                        // key is valid (i.e., channel not closed).
                        if (key.isValid && key.isWritable) {
                            // Log.i(TAG, "Have Something to WRITE");
                            handleWrite(key)
                        }
                        keyIter.remove()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Problem opening Selector", e)
            throw e
        }
        return null
    }

    internal class ClientRecord(var toAddress: SocketAddress) {
        var writeBuffer: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
    }

    companion object {
        private const val TAG = "UdpForwarder"
        private const val BUFFER_SIZE = 100000

        @Throws(IOException::class)
        fun handleRead(key: SelectionKey, readBuffer: ByteBuffer) {

            // Log.i("UdpForwarder", "Handling Read");
            val channel = key.channel() as DatagramChannel
            val clientRecord = key.attachment() as ClientRecord

            // Ensure the buffer is empty
            readBuffer.clear()

            // Receive the data
            channel.receive(readBuffer)

            // Get read to wrte, then send
            readBuffer.flip()
            channel.send(readBuffer, clientRecord.toAddress)

            // If there is anything remaining in the buffer
            if (readBuffer.remaining() > 0) {
                clientRecord.writeBuffer.put(readBuffer)
                key.interestOps(SelectionKey.OP_WRITE)
            }

//        ClientRecord clientRecord = (ClientRecord) key.attachment();
//        clientRecord.buffer.clear();    // Prepare buffer for receiving
//        clientRecord.clientAddress = channel.receive(clientRecord.buffer);
//
//        if (clientRecord.clientAddress != null) {  // Did we receive something?
//            // Register write with the selector
//            key.interestOps(SelectionKey.OP_WRITE);
//        }
        }

        @Throws(IOException::class)
        fun handleWrite(key: SelectionKey) {
            val channel = key.channel() as DatagramChannel
            val clientRecord = key.attachment() as ClientRecord
            clientRecord.writeBuffer.flip() // Prepare buffer for sending
            channel.send(clientRecord.writeBuffer, clientRecord.toAddress)
            if (clientRecord.writeBuffer.remaining() > 0) {
                clientRecord.writeBuffer.compact()
            } else {
                key.interestOps(SelectionKey.OP_READ)
                clientRecord.writeBuffer.clear()
            }

//        if (bytesSent != 0) { // Buffer completely written?
//            // No longer interested in writes
//            key.interestOps(SelectionKey.OP_READ);
//        }
        }
    }
}