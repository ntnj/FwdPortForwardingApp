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

package com.elixsr.portforwarder.forwarding;

import java.io.Serializable;

/**
 * The {@link ForwardingManager} class encapsulates all meta data related to the status of
 * forwarding throughout the application.
 * <p>
 * The class is a singleton, and can be accessed by any object to query the current status of
 * forwarding.
 */
public class ForwardingManager implements Serializable {

    private static ForwardingManager instance = null;


    private ForwardingManager() {

    }

    /**
     * Return an instance of the {@link ForwardingManager} class.
     *
     * @return
     */
    public static ForwardingManager getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (ForwardingManager.class) {
                if (instance == null) {
                    instance = new ForwardingManager();
                }
            }
        }
        return instance;
    }

    private boolean isEnabled = false;

    public boolean isEnabled() {
        return isEnabled;
    }

    protected void enableForwarding() {
        this.isEnabled = true;
    }

    protected void disableForwarding() {
        this.isEnabled = false;
    }


}
