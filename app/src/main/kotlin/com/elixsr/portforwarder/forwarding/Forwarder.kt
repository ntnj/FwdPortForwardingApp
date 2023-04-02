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

import java.net.InetSocketAddress
import java.util.concurrent.Callable

/**
 * The [Forwarder] class represents all details shared by a protocol forwarding class.
 *
 * @author Niall McShane
 */
abstract class Forwarder(
        /**
         * The name of the protocol being used to forward.
         */
        protected val protocol: String,
        /**
         * The from and target [InetSocketAddress] objects.
         */
        protected val from: InetSocketAddress, protected val to: InetSocketAddress?,
        /**
         * The name of the rule being forwarded.
         */
        protected val ruleName: String?) : Callable<Void?> {
    companion object {
        /**
         * Message to describe starting of port forwarding thread.
         */
        const val START_MESSAGE = "%s Port Forwarding Started from port %s to port %s"

        /**
         * Message to describe a failed binding from a Forwarding class.
         */
        const val BIND_FAILED_MESSAGE = "Could not bind port %s for %s Rule '%s'"

        /**
         * Message to describe a thread interruption.
         */
        const val THREAD_INTERRUPT_CLEANUP_MESSAGE = "%s Thread interrupted, will perform cleanup"
    }
}