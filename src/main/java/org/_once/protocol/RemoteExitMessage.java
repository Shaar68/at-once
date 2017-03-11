/* ============================================================================
 * RemoteExitMessage.java
 * 
 * Generated codec class for RemoteExitMessage
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

/**
 * RemoteExitMessage class.
 */
public class RemoteExitMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.REMOTE_EXIT;

    protected Integer version;
    protected String secret;
    protected String peer;
    protected String name;

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
     * @return The RemoteExitMessage, for method chaining
     */
    public RemoteExitMessage withSecret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * Get the peer field.
     * 
     * @return The peer field
     */
    public String getPeer() {
        return peer;
    }

    /**
     * Set the peer field.
     * 
     * @param peer The peer field
     */
    public void setPeer(String peer) {
        this.peer = peer;
    }

    /**
     * Set the peer field.
     *
     * @param peer The peer field
     * @return The RemoteExitMessage, for method chaining
     */
    public RemoteExitMessage withPeer(String peer) {
        this.peer = peer;
        return this;
    }

    /**
     * Get the name field.
     * 
     * @return The name field
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name field.
     * 
     * @param name The name field
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the name field.
     *
     * @param name The name field
     * @return The RemoteExitMessage, for method chaining
     */
    public RemoteExitMessage withName(String name) {
        this.name = name;
        return this;
    }
}
