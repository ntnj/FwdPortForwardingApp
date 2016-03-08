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
