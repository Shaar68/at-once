/* ============================================================================
 * ChallengeMessage.java
 * 
 * Generated codec class for ChallengeMessage
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
 * ChallengeMessage class.
 */
public class ChallengeMessage {
    public static final OnceSocket.MessageType MESSAGE_TYPE = OnceSocket.MessageType.CHALLENGE;

    protected Integer version;
    protected List<String> mechanisms;
    protected byte[] challenge = new byte[0];

    /**
     * Get the list of mechanisms strings.
     * 
     * @return The mechanisms strings
     */
    public List<String> getMechanisms() {
        if (mechanisms == null) {
            mechanisms = new ArrayList<>();
        }
        return mechanisms;
    }

    /**
     * Append a value to the mechanisms field.
     *
     * @param value The value
     */
    public void addMechanism(String value) {
        getMechanisms().add(value);
    }

    /**
     * Append a value to the mechanisms field.
     *
     * @param value The value
     * @return The ChallengeMessage, for method chaining
     */
    public ChallengeMessage withMechanism(String value) {
        getMechanisms().add(value);
        return this;
    }

    /**
     * Set the list of mechanisms strings.
     * 
     * @param mechanisms The mechanisms collection
     */
    public void setMechanisms(List<String> mechanisms) {
        this.mechanisms = mechanisms;
    }

    /**
     * Set the list of mechanisms strings.
     *
     * @param mechanisms The mechanisms collection
     * @return The ChallengeMessage, for method chaining
     */
    public ChallengeMessage withMechanisms(List<String> mechanisms) {
        this.mechanisms = mechanisms;
        return this;
    }

    /**
     * Get the challenge field.
     *
     * @return The challenge field
     */
    public byte[] getChallenge() {
        return challenge;
    }

    /**
     * Set the challenge field.
     *
     * @param challenge The challenge field
     */
    public void setChallenge(byte[] challenge) {
        this.challenge = new byte[challenge.length];
        System.arraycopy(challenge, 0, this.challenge, 0, challenge.length);
    }

    /**
     * Set the challenge field.
     *
     * @param challenge The challenge field
     * @return The ChallengeMessage, for method chaining
     */
    public ChallengeMessage withChallenge(byte[] challenge) {
        this.challenge = new byte[challenge.length];
        System.arraycopy(challenge, 0, this.challenge, 0, challenge.length);
        return this;
    }
}
