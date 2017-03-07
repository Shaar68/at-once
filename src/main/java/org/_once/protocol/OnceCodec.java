/* ============================================================================
 * OnceCodec.java
 *
 * Generated codec class for OnceCodec
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

import org.zeromq.api.Message;
import org.zeromq.api.Message.Frame;
import org.zeromq.api.Message.FrameBuilder;

/**
 * OnceCodec class.
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
public class OnceCodec {
    //  Protocol constants
    public static final int VERSION           = 1;

    //  Enumeration of message types
    public enum MessageType {
        CHALLENGE,
        AUTHENTICATE,
        GET_ENDPOINTS,
        LIST_ENDPOINTS,
        REMOTE_WHISPER,
        REMOTE_SHOUT
    }

    protected ChallengeMessage challenge;
    protected AuthenticateMessage authenticate;
    protected GetEndpointsMessage getEndpoints;
    protected ListEndpointsMessage listEndpoints;
    protected RemoteWhisperMessage remoteWhisper;
    protected RemoteShoutMessage remoteShout;

    /**
     * Deserialize a message.
     *
     * @return The MessageType of the deserialized message, or null
     */
    public MessageType deserialize(Message frames) {
        MessageType type = null;
        try {
            //  Read and parse command in frame
            Frame needle = frames.popFrame();

            //  Get and check protocol signature
            int signature = (0xffff) & needle.getShort();
            if (signature != (0xaaa0 | 2)) {
                return null;         //  Invalid signature
            }

            //  Get message id, which is first byte in frame
            int id = (0xff) & needle.getByte();
            type = MessageType.values()[id-1];
            switch (type) {
                case CHALLENGE: {
                    ChallengeMessage message = this.challenge = new ChallengeMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.mechanisms = needle.getClobs();
                    message.challenge = needle.getBytes();
                    break;
                }
                case AUTHENTICATE: {
                    AuthenticateMessage message = this.authenticate = new AuthenticateMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.mechanism = needle.getChars();
                    message.response = needle.getBytes();
                    break;
                }
                case GET_ENDPOINTS: {
                    GetEndpointsMessage message = this.getEndpoints = new GetEndpointsMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    break;
                }
                case LIST_ENDPOINTS: {
                    ListEndpointsMessage message = this.listEndpoints = new ListEndpointsMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.peers = needle.getClobs();
                    break;
                }
                case REMOTE_WHISPER: {
                    RemoteWhisperMessage message = this.remoteWhisper = new RemoteWhisperMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.peer = needle.getChars();
                    message.content = needle.getChars();
                    break;
                }
                case REMOTE_SHOUT: {
                    RemoteShoutMessage message = this.remoteShout = new RemoteShoutMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.group = needle.getChars();
                    message.content = needle.getChars();
                    break;
                }
                default:
                    throw new IllegalArgumentException("Invalid message: unrecognized type: " + type);
            }

            return type;
        } catch (Exception ex) {
            //  Error returns
            System.err.printf("E: Malformed message: %s\n", type);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a CHALLENGE message from the socket.
     *
     * @return The ChallengeMessage last received on this socket
     */
    public ChallengeMessage getChallenge() {
        return challenge;
    }

    /**
     * Get a AUTHENTICATE message from the socket.
     *
     * @return The AuthenticateMessage last received on this socket
     */
    public AuthenticateMessage getAuthenticate() {
        return authenticate;
    }

    /**
     * Get a GET_ENDPOINTS message from the socket.
     *
     * @return The GetEndpointsMessage last received on this socket
     */
    public GetEndpointsMessage getGetEndpoints() {
        return getEndpoints;
    }

    /**
     * Get a LIST_ENDPOINTS message from the socket.
     *
     * @return The ListEndpointsMessage last received on this socket
     */
    public ListEndpointsMessage getListEndpoints() {
        return listEndpoints;
    }

    /**
     * Get a REMOTE_WHISPER message from the socket.
     *
     * @return The RemoteWhisperMessage last received on this socket
     */
    public RemoteWhisperMessage getRemoteWhisper() {
        return remoteWhisper;
    }

    /**
     * Get a REMOTE_SHOUT message from the socket.
     *
     * @return The RemoteShoutMessage last received on this socket
     */
    public RemoteShoutMessage getRemoteShout() {
        return remoteShout;
    }

    /**
     * Send the CHALLENGE to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(ChallengeMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 1);       //  Message ID

        builder.putByte((byte) 1);
        if (message.mechanisms != null) {
            builder.putClobs(message.mechanisms);
        } else {
            builder.putInt(0);   //  Empty string array
        }
        builder.putBytes(message.challenge);

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }

    /**
     * Send the AUTHENTICATE to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(AuthenticateMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 2);       //  Message ID

        builder.putByte((byte) 1);
        if (message.mechanism != null) {
            builder.putString(message.mechanism);
        } else {
            builder.putString("");        //  Empty string
        }
        builder.putBytes(message.response);

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }

    /**
     * Send the GET_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(GetEndpointsMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 3);       //  Message ID

        builder.putByte((byte) 1);

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }

    /**
     * Send the LIST_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(ListEndpointsMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 4);       //  Message ID

        builder.putByte((byte) 1);
        if (message.peers != null) {
            builder.putClobs(message.peers);
        } else {
            builder.putInt(0);   //  Empty string array
        }

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }

    /**
     * Send the REMOTE_WHISPER to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(RemoteWhisperMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 5);       //  Message ID

        builder.putByte((byte) 1);
        if (message.peer != null) {
            builder.putString(message.peer);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.content != null) {
            builder.putString(message.content);
        } else {
            builder.putString("");        //  Empty string
        }

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }

    /**
     * Send the REMOTE_SHOUT to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(RemoteShoutMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 6);       //  Message ID

        builder.putByte((byte) 1);
        if (message.group != null) {
            builder.putString(message.group);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.content != null) {
            builder.putString(message.content);
        } else {
            builder.putString("");        //  Empty string
        }

        //  Create multi-frame message
        Message frames = new Message();

        //  Now add the data frame
        frames.addFrame(builder.build());

        return frames;
    }
}

