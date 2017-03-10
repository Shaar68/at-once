/* ============================================================================
 * RemoteShoutMessage.java
 * 
 * Generated codec class for RemoteShoutMessage
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
 * RemoteShoutMessage class.
 */
public class RemoteShoutMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.REMOTE_SHOUT;

    protected Integer version;
    protected String group;
    protected String content;

    /**
     * Get the group field.
     * 
     * @return The group field
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set the group field.
     * 
     * @param group The group field
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Set the group field.
     *
     * @param group The group field
     * @return The RemoteShoutMessage, for method chaining
     */
    public RemoteShoutMessage withGroup(String group) {
        this.group = group;
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
     * @return The RemoteShoutMessage, for method chaining
     */
    public RemoteShoutMessage withContent(String content) {
        this.content = content;
        return this;
    }
}
