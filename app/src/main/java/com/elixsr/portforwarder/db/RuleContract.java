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

package com.elixsr.portforwarder.db;

import android.provider.BaseColumns;

/**
 * Created by Niall McShane on 07/03/2016.
 *
 * Sourced from: http://developer.android.com/training/basics/data-storage/databases.html#DefineContract
 */
public class RuleContract {

    public RuleContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class RuleEntry implements BaseColumns {
        public static final String TABLE_NAME = "rule";
        public static final String COLUMN_NAME_RULE_ID = "rule_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IS_TCP = "is_tcp";
        public static final String COLUMN_NAME_IS_UDP = "is_udp";
        public static final String COLUMN_NAME_FROM_INTERFACE_NAME = "from_interface_name";
        public static final String COLUMN_NAME_FROM_PORT = "from_port";
        public static final String COLUMN_NAME_TARGET_IP_ADDRESS = "target_ip_address";
        public static final String COLUMN_NAME_TARGET_PORT = "target_port";
    }
}
