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
    protected List<String> endpoints;

    /**
     * Get the list of endpoints strings.
     * 
     * @return The endpoints strings
     */
    public List<String> getEndpoints() {
        if (endpoints == null) {
            endpoints = new ArrayList<>();
        }
        return endpoints;
    }

    /**
     * Append a value to the endpoints field.
     *
     * @param value The value
     */
    public void addEndpoint(String value) {
        getEndpoints().add(value);
    }

    /**
     * Append a value to the endpoints field.
     *
     * @param value The value
     * @return The ListEndpointsMessage, for method chaining
     */
    public ListEndpointsMessage withEndpoint(String value) {
        getEndpoints().add(value);
        return this;
    }

    /**
     * Set the list of endpoints strings.
     * 
     * @param endpoints The endpoints collection
     */
    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * Set the list of endpoints strings.
     *
     * @param endpoints The endpoints collection
     * @return The ListEndpointsMessage, for method chaining
     */
    public ListEndpointsMessage withEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
        return this;
    }
}
