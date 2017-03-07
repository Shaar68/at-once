/* ============================================================================
 * ListEndpointsMessage.java
 * 
 * Generated codec class for ListEndpointsMessage
 * ----------------------------------------------------------------------------
 * Copyright (c) 2017 InSource Software -- http://www.insource.io          
 * Copyright other contributors as noted in the AUTHORS file.              
 *                                                                         
 * This file is part of @Once, an open-source framework for proximity-based
 * peer-to-peer applications -- See http://zyre.org.                       
 *                                                                         
 * This Source Code Form is subject to the terms of the Mozilla Public     
 * License, v. 2.0. If a copy of the MPL was not distributed with this     
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.                
 * ============================================================================
 */
package org._once.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * ListEndpointsMessage class.
 */
public class ListEndpointsMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.LIST_ENDPOINTS;

    protected Integer version;
    protected List<String> peers;

    /**
     * Get the list of peers strings.
     * 
     * @return The peers strings
     */
    public List<String> getPeers() {
        if (peers == null) {
            peers = new ArrayList<>();
        }
        return peers;
    }

    /**
     * Append a value to the peers field.
     *
     * @param value The value
     */
    public void addPeer(String value) {
        getPeers().add(value);
    }

    /**
     * Append a value to the peers field.
     *
     * @param value The value
     * @return The ListEndpointsMessage, for method chaining
     */
    public ListEndpointsMessage withPeer(String value) {
        getPeers().add(value);
        return this;
    }

    /**
     * Set the list of peers strings.
     * 
     * @param peers The peers collection
     */
    public void setPeers(List<String> peers) {
        this.peers = peers;
    }

    /**
     * Set the list of peers strings.
     *
     * @param peers The peers collection
     * @return The ListEndpointsMessage, for method chaining
     */
    public ListEndpointsMessage withPeers(List<String> peers) {
        this.peers = peers;
        return this;
    }
}
