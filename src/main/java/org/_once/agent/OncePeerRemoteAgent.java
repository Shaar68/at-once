/* ============================================================================
 * OncePeerRemoteAgent.java
 *
 * Generated class for OncePeerRemoteAgent
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
package org._once.agent;

import org.zeromq.api.LoopAdapter;
import org.zeromq.api.Message;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;

/**
 * OncePeerRemoteAgent class.
 *
 * @author sriesenberg
 */
public class OncePeerRemoteAgent extends LoopAdapter {
    // Application callback handler
    private Handler handler;

    // Structure of our class
    private Reactor reactor;
    private Socket socket;
    private Message message;
    private State state = State.START;
    private Event event;
    private Event next;

    public OncePeerRemoteAgent(Handler handler) {
        this.handler = handler;
    }

    public Reactor getReactor() {
        return reactor;
    }

    public Socket getSocket() {
        return socket;
    }

    public Message getMessage() {
        return message;
    }

    public State getState() {
        return state;
    }

    public Event getEvent() {
        return event;
    }

    public Event getNext() {
        return next;
    }

    public void triggerEvent(Event next) {
        this.next = next;
    }

    @Override
    protected void execute(Reactor reactor, Socket socket) {
        this.reactor = reactor;
        this.socket = socket;

        message = socket.receiveMessage();
        next = Event.valueOf(message.popString());
        while (next != null) {
            event = next;
            next = null;
            execute();
        }
    }

    protected void execute() {
        switch (state) {
            case START: {
                switch (event) {
                    case ENTER: {
                        handler.onServerConnect(this);
                        state = State.AUTHENTICATING;
                        break;
                    }
                }
                break;
            }
            case AUTHENTICATING: {
                switch (event) {
                    case CHALLENGE: {
                        handler.onChallenge(this);
                        // TODO: send AUTHENTICATE
                        break;
                    }
                    case OK: {
                        handler.onOk(this);
                        // TODO: send GET ENDPOINTS
                        state = State.CONNECTING;
                        break;
                    }
                    case NOPE: {
                        reactor.cancel(this);
                        break;
                    }
                    case WHISPER: {
                        handler.checkSharedSecret(this);
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.checkSharedSecret(this);
                        handler.onShout(this);
                        break;
                    }
                }
                break;
            }
            case CONNECTING: {
                switch (event) {
                    case LIST_ENDPOINTS: {
                        handler.onListEndpoints(this);
                        state = State.READY;
                        break;
                    }
                    case WHISPER: {
                        handler.checkSharedSecret(this);
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.checkSharedSecret(this);
                        handler.onShout(this);
                        break;
                    }
                }
                break;
            }
            case READY: {
                switch (event) {
                    case ENTER: {
                        handler.onEnter(this);
                        // TODO: send GET PEERS
                        break;
                    }
                    case JOIN: {
                        handler.onJoin(this);
                        break;
                    }
                    case LEAVE: {
                        handler.onLeave(this);
                        break;
                    }
                    case EXIT: {
                        handler.onExit(this);
                        break;
                    }
                    case GET_PEERS: {
                        handler.onGetPeers(this);
                        // TODO: send LIST PEERS
                        break;
                    }
                    case LIST_PEERS: {
                        handler.onListPeers(this);
                        break;
                    }
                    case REMOTE_ENTER: {
                        handler.onRemoteEnter(this);
                        break;
                    }
                    case REMOTE_EXIT: {
                        handler.onRemoteExit(this);
                        break;
                    }
                    case REMOTE_WHISPER: {
                        handler.onRemoteWhisper(this);
                        break;
                    }
                    case REMOTE_SHOUT: {
                        handler.onRemoteShout(this);
                        break;
                    }
                    case STOP: {
                        reactor.cancel(this);
                        break;
                    }
                    case WHISPER: {
                        handler.checkSharedSecret(this);
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.checkSharedSecret(this);
                        handler.onShout(this);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * States we can be in.
     */
    public enum State {
        START,
        AUTHENTICATING,
        CONNECTING,
        READY
    }

    /**
     * Events we can process.
     */
    public enum Event {
        ENTER,
        CHALLENGE,
        OK,
        NOPE,
        LIST_ENDPOINTS,
        JOIN,
        LEAVE,
        EXIT,
        GET_PEERS,
        LIST_PEERS,
        REMOTE_ENTER,
        REMOTE_EXIT,
        REMOTE_WHISPER,
        REMOTE_SHOUT,
        STOP,
        WHISPER,
        SHOUT
    }

    /**
     * Application callback handler interface.
     */
    public interface Handler {
        /**
         * Callback for the "on server connect" action.
         *
         * @param agent Handle to the agent instance
         */
        void onServerConnect(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on challenge" action.
         *
         * @param agent Handle to the agent instance
         */
        void onChallenge(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on ok" action.
         *
         * @param agent Handle to the agent instance
         */
        void onOk(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on list endpoints" action.
         *
         * @param agent Handle to the agent instance
         */
        void onListEndpoints(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on enter" action.
         *
         * @param agent Handle to the agent instance
         */
        void onEnter(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on join" action.
         *
         * @param agent Handle to the agent instance
         */
        void onJoin(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on leave" action.
         *
         * @param agent Handle to the agent instance
         */
        void onLeave(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on exit" action.
         *
         * @param agent Handle to the agent instance
         */
        void onExit(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on get peers" action.
         *
         * @param agent Handle to the agent instance
         */
        void onGetPeers(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on list peers" action.
         *
         * @param agent Handle to the agent instance
         */
        void onListPeers(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on remote enter" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteEnter(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on remote exit" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteExit(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on remote whisper" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteWhisper(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on remote shout" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteShout(OncePeerRemoteAgent agent);

        /**
         * Callback for the "check shared secret" action.
         *
         * @param agent Handle to the agent instance
         */
        void checkSharedSecret(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on whisper" action.
         *
         * @param agent Handle to the agent instance
         */
        void onWhisper(OncePeerRemoteAgent agent);

        /**
         * Callback for the "on shout" action.
         *
         * @param agent Handle to the agent instance
         */
        void onShout(OncePeerRemoteAgent agent);
    }
}
