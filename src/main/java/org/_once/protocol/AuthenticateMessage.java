/* ============================================================================
 * AuthenticateMessage.java
 * 
 * Generated codec class for AuthenticateMessage
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
 * AuthenticateMessage class.
 */
public class AuthenticateMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.AUTHENTICATE;

    protected Integer version;
    protected String mechanism;
    protected byte[] response = new byte[0];

    /**
     * Get the mechanism field.
     * 
     * @return The mechanism field
     */
    public String getMechanism() {
        return mechanism;
    }

    /**
     * Set the mechanism field.
     * 
     * @param mechanism The mechanism field
     */
    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    /**
     * Set the mechanism field.
     *
     * @param mechanism The mechanism field
     * @return The AuthenticateMessage, for method chaining
     */
    public AuthenticateMessage withMechanism(String mechanism) {
        this.mechanism = mechanism;
        return this;
    }

    /**
     * Get the response field.
     *
     * @return The response field
     */
    public byte[] getResponse() {
        return response;
    }

    /**
     * Set the response field.
     *
     * @param response The response field
     */
    public void setResponse(byte[] response) {
        this.response = new byte[response.length];
        System.arraycopy(response, 0, this.response, 0, response.length);
    }

    /**
     * Set the response field.
     *
     * @param response The response field
     * @return The AuthenticateMessage, for method chaining
     */
    public AuthenticateMessage withResponse(byte[] response) {
        this.response = new byte[response.length];
        System.arraycopy(response, 0, this.response, 0, response.length);
        return this;
    }
}
