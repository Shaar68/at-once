/* ============================================================================
 * GetEndpointsMessage.java
 * 
 * Generated codec class for GetEndpointsMessage
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
 * GetEndpointsMessage class.
 */
public class GetEndpointsMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.GET_ENDPOINTS;

    protected Integer version;
    protected String token;

    /**
     * Get the token field.
     * 
     * @return The token field
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the token field.
     * 
     * @param token The token field
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Set the token field.
     *
     * @param token The token field
     * @return The GetEndpointsMessage, for method chaining
     */
    public GetEndpointsMessage withToken(String token) {
        this.token = token;
        return this;
    }
}
