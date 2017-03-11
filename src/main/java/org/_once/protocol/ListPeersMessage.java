/* ============================================================================
 * ListPeersMessage.java
 * 
 * Generated codec class for ListPeersMessage
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

import java.util.HashMap;
import java.util.Map;

/**
 * ListPeersMessage class.
 */
public class ListPeersMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.LIST_PEERS;

    protected Integer version;
    protected String secret;
    protected Map<String, String> peers;

    /**
     * Get the secret field.
     * 
     * @return The secret field
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Set the secret field.
     * 
     * @param secret The secret field
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Set the secret field.
     *
     * @param secret The secret field
     * @return The ListPeersMessage, for method chaining
     */
    public ListPeersMessage withSecret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * Get the the peers hash.
     * 
     * @return The peers hash
     */
    public Map<String, String> getPeers() {
        if (peers == null) {
            peers = new HashMap<>();
        }
        return peers;
    }

    /**
     * Get a value in the peers hash as a string.
     * 
     * @param key The hash key
     * @param defaultValue The default value if the key does not exist
     * @return The hash value, or the default value if the key does not exist
     */
    public String getPeer(String key, String defaultValue) {
        String value = defaultValue;
        if (peers != null && peers.containsKey(key)) {
            value = peers.get(key);
        }
        return value;
    }

    /**
     * Get a value in the peers hash as a long.
     * 
     * @param key The hash key
     * @param defaultValue The default value if the key does not exist
     * @return The hash value, or the default value if the key does not exist
     */
    public long getPeer(String key, long defaultValue) {
        long value = defaultValue;
        if (peers != null && peers.containsKey(key)) {
            value = Long.parseLong(peers.get(key));
        }
        return value;
    }

    /**
     * Get a value in the peers hash as a long.
     *
     * @param key The hash key
     * @param defaultValue The default value if the key does not exist
     * @return The hash value, or the default value if the key does not exist
     */
    public int getPeer(String key, int defaultValue) {
        int value = defaultValue;
        if (peers != null && peers.containsKey(key)) {
            value = Integer.parseInt(peers.get(key));
        }
        return value;
    }

    /**
     * Set a value in the peers hash.
     *
     * @param key The hash key
     * @param value The value
     */
    public void putPeer(String key, String value) {
        getPeers().put(key, value);
    }

    /**
     * Set a value in the peers hash.
     *
     * @param key The hash key
     * @param value The value
     * @return The ListPeersMessage, for method chaining
     */
    public ListPeersMessage withPeer(String key, String value) {
        getPeers().put(key, value);
        return this;
    }

    /**
     * Set a value in the peers hash.
     * 
     * @param key The hash key
     * @param value The value
     */
    public void putPeer(String key, int value) {
        getPeers().put(key, String.valueOf(value));
    }

    /**
     * Set a value in the peers hash.
     *
     * @param key The hash key
     * @param value The value
     * @return The ListPeersMessage, for method chaining
     */
    public ListPeersMessage withPeer(String key, int value) {
        getPeers().put(key, String.valueOf(value));
        return this;
    }

    /**
     * Set a value in the peers hash.
     * 
     * @param key The hash key
     * @param value The value
     */
    public void putPeer(String key, long value) {
        getPeers().put(key, String.valueOf(value));
    }

    /**
     * Set a value in the peers hash.
     *
     * @param key The hash key
     * @param value The value
     * @return The ListPeersMessage, for method chaining
     */
    public ListPeersMessage withPeer(String key, long value) {
        getPeers().put(key, String.valueOf(value));
        return this;
    }

    /**
     * Set the peers hash.
     * 
     * @param peers The new peers hash
     */
    public void setPeers(Map<String, String> peers) {
        this.peers = peers;
    }

    /**
     * Set the peers hash.
     *
     * @param peers The new peers hash
     * @return The ListPeersMessage, for method chaining
     */
    public ListPeersMessage withPeers(Map<String, String> peers) {
        this.peers = peers;
        return this;
    }
}
