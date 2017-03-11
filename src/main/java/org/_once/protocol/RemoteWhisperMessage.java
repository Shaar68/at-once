/* ============================================================================
 * RemoteWhisperMessage.java
 * 
 * Generated codec class for RemoteWhisperMessage
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
 * RemoteWhisperMessage class.
 */
public class RemoteWhisperMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.REMOTE_WHISPER;

    protected Integer version;
    protected String secret;
    protected String from;
    protected String peer;
    protected String content;

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
     * @return The RemoteWhisperMessage, for method chaining
     */
    public RemoteWhisperMessage withSecret(String secret) {
        this.secret = secret;
        return this;
    }

    /**
     * Get the from field.
     * 
     * @return The from field
     */
    public String getFrom() {
        return from;
    }

    /**
     * Set the from field.
     * 
     * @param from The from field
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the from field.
     *
     * @param from The from field
     * @return The RemoteWhisperMessage, for method chaining
     */
    public RemoteWhisperMessage withFrom(String from) {
        this.from = from;
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
     * @return The RemoteWhisperMessage, for method chaining
     */
    public RemoteWhisperMessage withPeer(String peer) {
        this.peer = peer;
        return this;
    }

    /**
     * Get the content field.
     * 
     * @return The content field
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content field.
     * 
     * @param content The content field
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Set the content field.
     *
     * @param content The content field
     * @return The RemoteWhisperMessage, for method chaining
     */
    public RemoteWhisperMessage withContent(String content) {
        this.content = content;
        return this;
    }
}
