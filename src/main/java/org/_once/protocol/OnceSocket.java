/* ============================================================================
 * OnceSocket.java
 * 
 * Generated codec class for OnceSocket
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

import org.zeromq.ZMQ;
import org.zeromq.api.Message;
import org.zeromq.api.Message.Frame;
import org.zeromq.api.Socket;

import java.io.Closeable;

/**
 * OnceSocket class.
 * 
 * The specification for this class is as follows:
 * <pre class="text">
 *  CHALLENGE - Authentication challenge to be answered by a peer.
 *    version                      number 1
 *    mechanisms                   strings
 *    challenge                    bytes []
 *  AUTHENTICATE - Authentication response used to answer a challenge sent by a peer or the server.
 *    version                      number 1
 *    mechanism                    string
 *    response                     bytes []
 *  GET_ENDPOINTS - Get a list of peers connected to the server.
 *    version                      number 1
 *  LIST_ENDPOINTS - Send a list of peers connected to the server.
 *    version                      number 1
 *    peers                        strings
 *  REMOTE_WHISPER - Relay a whisper message through a bridge node.
 *    version                      number 1
 *    peer                         string
 *    content                      string
 *  REMOTE_SHOUT - Relay a shout through a bridge node.
 *    version                      number 1
 *    group                        string
 *    content                      string
 * </pre>
 * 
 * @author sriesenberg
 */
public class OnceSocket extends OnceCodec implements Closeable {
    //  Structure of our class
    private Socket socket;        //  Internal socket handle
    private Frame address;        //  Address of peer if any

    /**
     * Create a new OnceSocket.
     * 
     * @param socket The internal socket
     */
    public OnceSocket(Socket socket) {
        assert socket != null;
        this.socket = socket;
    }

    /**
     * Get the message address.
     * 
     * @return The message address frame
     */
    public Frame getAddress() {
        return address;
    }

    /**
     * Set the message address.
     * 
     * @param address The new message address
     */
    public void setAddress(Frame address) {
        this.address = address;
    }

    /**
     * Get the internal socket.
     *
     * @return The internal socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Destroy the OnceSocket.
     */
    @Override
    public void close() {
        socket.close();
    }

    /**
     * Receive a message on the socket.
     *
     * @return The MessageType of the received message
     */
    public MessageType receive() {
        //  Read valid message frame from socket; we loop over any
        //  garbage data we might receive from badly-connected peers
        MessageType type;
        Message frames;
        do {
            frames = socket.receiveMessage();

            //  If we're reading from a ROUTER socket, get address
            if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
                this.address = frames.popFrame();
            }

            //  Get and check protocol signature
            type = deserialize(frames);
        } while (type == null);          //  Protocol assertion, drop message if malformed or invalid

        return type;
    }

    /**
     * Send the CHALLENGE to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(ChallengeMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }

    /**
     * Send the AUTHENTICATE to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(AuthenticateMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }

    /**
     * Send the GET_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(GetEndpointsMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }

    /**
     * Send the LIST_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(ListEndpointsMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }

    /**
     * Send the REMOTE_WHISPER to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(RemoteWhisperMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }

    /**
     * Send the REMOTE_SHOUT to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public boolean send(RemoteShoutMessage message) {
        Message frames = serialize(message);

        //  If we're sending to a ROUTER, we add the address first
        if (socket.getZMQSocket().getType() == ZMQ.ROUTER) {
            assert address != null;
            frames.pushFrame(address);
        }

        return socket.send(frames);
    }
}

