/* ============================================================================
 * GetPeersMessage.java
 * 
 * Generated codec class for GetPeersMessage
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
 * GetPeersMessage class.
 */
public class GetPeersMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.GET_PEERS;

    protected Integer version;
    protected String secret;

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
     * @return The GetPeersMessage, for method chaining
     */
    public GetPeersMessage withSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
