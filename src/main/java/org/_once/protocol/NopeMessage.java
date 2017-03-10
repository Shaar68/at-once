/* ============================================================================
 * NopeMessage.java
 * 
 * Generated codec class for NopeMessage
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
 * NopeMessage class.
 */
public class NopeMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.NOPE;

    protected Integer version;
    protected Integer statusCode;
    protected String statusText;

    /**
     * Get the statusCode field.
     * 
     * @return The statusCode field
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * Set the statusCode field.
     * 
     * @param statusCode The statusCode field
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Set the statusCode field.
     *
     * @param statusCode The statusCode field
     * @return The NopeMessage, for method chaining
     */
    public NopeMessage withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Get the statusText field.
     * 
     * @return The statusText field
     */
    public String getStatusText() {
        return statusText;
    }

    /**
     * Set the statusText field.
     * 
     * @param statusText The statusText field
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * Set the statusText field.
     *
     * @param statusText The statusText field
     * @return The NopeMessage, for method chaining
     */
    public NopeMessage withStatusText(String statusText) {
        this.statusText = statusText;
        return this;
    }
}
