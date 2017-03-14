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
 *  AUTHENTICATE - Authentication request used to answer a challenge sent by a peer or the server.
 *    version                      number 1
 *    mechanism                    string
 *    response                     bytes []
 *  OK - Authentication response indicating successful authentication.
 *    version                      number 1
 *    token                        string
 *    secret                       string
 *  NOPE - Authentication response indicating unsuccessful authentication.
 *    version                      number 1
 *    statusCode                   number 4
 *    statusText                   string
 *  GET_ENDPOINTS - Get a list of peers connected to the server.
 *    version                      number 1
 *    token                        string
 *  LIST_ENDPOINTS - Send a list of peers connected to the server.
 *    version                      number 1
 *    token                        string
 *    endpoints                    strings
 *  GET_PEERS - Get a list of peers connected to the peer on the remote network.
 *    version                      number 1
 *    secret                       string
 *  LIST_PEERS - Send a list of peers connected to the peer on the remote network.
 *    version                      number 1
 *    secret                       string
 *    peers                        hash
 *  REMOTE_WHISPER - Relay a whisper message through a bridge node.
 *    version                      number 1
 *    secret                       string
 *    from                         string
 *    peer                         string
 *    content                      string
 *  REMOTE_SHOUT - Relay a shout through a bridge node.
 *    version                      number 1
 *    secret                       string
 *    from                         string
 *    group                        string
 *    content                      string
 *  REMOTE_ENTER - Relay a remote enter event through a bridge node.
 *    version                      number 1
 *    secret                       string
 *    peer                         string
 *    name                         string
 *  REMOTE_EXIT - Relay a remote exit event through a bridge node.
 *    version                      number 1
 *    secret                       string
 *    peer                         string
 *    name                         string
 *  STOP - Message indicating the peer should exit.
 *    version                      number 1
 *    token                        string
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
        OK,
        NOPE,
        GET_ENDPOINTS,
        LIST_ENDPOINTS,
        GET_PEERS,
        LIST_PEERS,
        REMOTE_WHISPER,
        REMOTE_SHOUT,
        REMOTE_ENTER,
        REMOTE_EXIT,
        STOP
    }

    protected ChallengeMessage challenge;
    protected AuthenticateMessage authenticate;
    protected OkMessage ok;
    protected NopeMessage nope;
    protected GetEndpointsMessage getEndpoints;
    protected ListEndpointsMessage listEndpoints;
    protected GetPeersMessage getPeers;
    protected ListPeersMessage listPeers;
    protected RemoteWhisperMessage remoteWhisper;
    protected RemoteShoutMessage remoteShout;
    protected RemoteEnterMessage remoteEnter;
    protected RemoteExitMessage remoteExit;
    protected StopMessage stop;

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
                    message.mechanism = needle.getString();
                    message.response = needle.getBytes();
                    break;
                }
                case OK: {
                    OkMessage message = this.ok = new OkMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.token = needle.getString();
                    message.secret = needle.getString();
                    break;
                }
                case NOPE: {
                    NopeMessage message = this.nope = new NopeMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.statusCode = needle.getInt();
                    message.statusText = needle.getString();
                    break;
                }
                case GET_ENDPOINTS: {
                    GetEndpointsMessage message = this.getEndpoints = new GetEndpointsMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.token = needle.getString();
                    break;
                }
                case LIST_ENDPOINTS: {
                    ListEndpointsMessage message = this.listEndpoints = new ListEndpointsMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.token = needle.getString();
                    message.endpoints = needle.getClobs();
                    break;
                }
                case GET_PEERS: {
                    GetPeersMessage message = this.getPeers = new GetPeersMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    break;
                }
                case LIST_PEERS: {
                    ListPeersMessage message = this.listPeers = new ListPeersMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    message.peers = needle.getMap();
                    break;
                }
                case REMOTE_WHISPER: {
                    RemoteWhisperMessage message = this.remoteWhisper = new RemoteWhisperMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    message.from = needle.getString();
                    message.peer = needle.getString();
                    message.content = needle.getString();
                    break;
                }
                case REMOTE_SHOUT: {
                    RemoteShoutMessage message = this.remoteShout = new RemoteShoutMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    message.from = needle.getString();
                    message.group = needle.getString();
                    message.content = needle.getString();
                    break;
                }
                case REMOTE_ENTER: {
                    RemoteEnterMessage message = this.remoteEnter = new RemoteEnterMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    message.peer = needle.getString();
                    message.name = needle.getString();
                    break;
                }
                case REMOTE_EXIT: {
                    RemoteExitMessage message = this.remoteExit = new RemoteExitMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.secret = needle.getString();
                    message.peer = needle.getString();
                    message.name = needle.getString();
                    break;
                }
                case STOP: {
                    StopMessage message = this.stop = new StopMessage();
                    message.version = (0xff) & needle.getByte();
                    if (message.version != 1) {
                        throw new IllegalArgumentException();
                    }
                    message.token = needle.getString();
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
     * Get a OK message from the socket.
     *
     * @return The OkMessage last received on this socket
     */
    public OkMessage getOk() {
        return ok;
    }

    /**
     * Get a NOPE message from the socket.
     *
     * @return The NopeMessage last received on this socket
     */
    public NopeMessage getNope() {
        return nope;
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
     * Get a GET_PEERS message from the socket.
     *
     * @return The GetPeersMessage last received on this socket
     */
    public GetPeersMessage getGetPeers() {
        return getPeers;
    }

    /**
     * Get a LIST_PEERS message from the socket.
     *
     * @return The ListPeersMessage last received on this socket
     */
    public ListPeersMessage getListPeers() {
        return listPeers;
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
     * Get a REMOTE_ENTER message from the socket.
     *
     * @return The RemoteEnterMessage last received on this socket
     */
    public RemoteEnterMessage getRemoteEnter() {
        return remoteEnter;
    }

    /**
     * Get a REMOTE_EXIT message from the socket.
     *
     * @return The RemoteExitMessage last received on this socket
     */
    public RemoteExitMessage getRemoteExit() {
        return remoteExit;
    }

    /**
     * Get a STOP message from the socket.
     *
     * @return The StopMessage last received on this socket
     */
    public StopMessage getStop() {
        return stop;
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
     * Send the OK to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(OkMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 3);       //  Message ID

        builder.putByte((byte) 1);
        if (message.token != null) {
            builder.putString(message.token);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.secret != null) {
            builder.putString(message.secret);
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
     * Send the NOPE to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(NopeMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 4);       //  Message ID

        builder.putByte((byte) 1);
        builder.putInt(message.statusCode);
        if (message.statusText != null) {
            builder.putString(message.statusText);
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
     * Send the GET_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(GetEndpointsMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 5);       //  Message ID

        builder.putByte((byte) 1);
        if (message.token != null) {
            builder.putString(message.token);
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
     * Send the LIST_ENDPOINTS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(ListEndpointsMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 6);       //  Message ID

        builder.putByte((byte) 1);
        if (message.token != null) {
            builder.putString(message.token);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.endpoints != null) {
            builder.putClobs(message.endpoints);
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
     * Send the GET_PEERS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(GetPeersMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 7);       //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
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
     * Send the LIST_PEERS to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(ListPeersMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 8);       //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.peers != null) {
            builder.putMap(message.peers);
        } else {
            builder.putInt(0);   //  Empty hash
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
        builder.putByte((byte) 9);       //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.from != null) {
            builder.putString(message.from);
        } else {
            builder.putString("");        //  Empty string
        }
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
        builder.putByte((byte) 10);      //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.from != null) {
            builder.putString(message.from);
        } else {
            builder.putString("");        //  Empty string
        }
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

    /**
     * Send the REMOTE_ENTER to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(RemoteEnterMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 11);      //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.peer != null) {
            builder.putString(message.peer);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.name != null) {
            builder.putString(message.name);
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
     * Send the REMOTE_EXIT to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(RemoteExitMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 12);      //  Message ID

        builder.putByte((byte) 1);
        if (message.secret != null) {
            builder.putString(message.secret);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.peer != null) {
            builder.putString(message.peer);
        } else {
            builder.putString("");        //  Empty string
        }
        if (message.name != null) {
            builder.putString(message.name);
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
     * Send the STOP to the socket in one step.
     *
     * @param message The message to send
     * @return true if the message was sent, false otherwise
     */
    public Message serialize(StopMessage message) {
        //  Now serialize message into the frame
        FrameBuilder builder = new FrameBuilder();
        builder.putShort((short) (0xaaa0 | 2));
        builder.putByte((byte) 13);      //  Message ID

        builder.putByte((byte) 1);
        if (message.token != null) {
            builder.putString(message.token);
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

