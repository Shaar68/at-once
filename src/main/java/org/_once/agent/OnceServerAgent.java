/* ============================================================================
 * OnceServerAgent.java
 *
 * Generated class for OnceServerAgent
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
 * OnceServerAgent class.
 *
 * @author sriesenberg
 */
public class OnceServerAgent extends LoopAdapter {
    // Application callback handler
    private Handler handler;

    // Structure of our class
    private Reactor reactor;
    private Socket socket;
    private Message message;
    private State state = State.START;
    private Event event;
    private Event next;

    public OnceServerAgent(Handler handler) {
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
                        handler.onEnter(this);
                        state = State.RESPONDING;
                        next = Event.CHALLENGE;
                        break;
                    }
                }
                break;
            }
            case READY: {
                switch (event) {
                    case ENTER: {
                        handler.onEnter(this);
                        state = State.RESPONDING;
                        next = Event.CHALLENGE;
                        break;
                    }
                    case JOIN: {
                        handler.onJoin(this);
                        break;
                    }
                    case WHISPER: {
                        handler.checkAuthenticationToken(this);
                        handler.onWhisper(this);
                        state = State.RESPONDING;
                        break;
                    }
                    case SHOUT: {
                        handler.onShout(this);
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
                }
                break;
            }
            case RESPONDING: {
                switch (event) {
                    case CHALLENGE: {
                        // TODO: send CHALLENGE
                        state = State.READY;
                        break;
                    }
                    case AUTHENTICATE: {
                        handler.onAuthenticate(this);
                        state = State.AUTHENTICATING;
                        break;
                    }
                    case UNAUTHORIZED: {
                        handler.onUnauthorized(this);
                        // TODO: send NOPE
                        state = State.READY;
                        break;
                    }
                    case GET_ENDPOINTS: {
                        handler.onGetEndpoints(this);
                        // TODO: send LIST ENDPOINTS
                        state = State.READY;
                        break;
                    }
                }
                break;
            }
            case AUTHENTICATING: {
                switch (event) {
                    case AUTHENTICATE_OK: {
                        // TODO: send OK
                        state = State.READY;
                        break;
                    }
                    case AUTHENTICATE_FAILED: {
                        // TODO: send NOPE
                        state = State.READY;
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
        READY,
        RESPONDING,
        AUTHENTICATING
    }

    /**
     * Events we can process.
     */
    public enum Event {
        ENTER,
        JOIN,
        WHISPER,
        SHOUT,
        LEAVE,
        EXIT,
        CHALLENGE,
        AUTHENTICATE,
        UNAUTHORIZED,
        GET_ENDPOINTS,
        AUTHENTICATE_OK,
        AUTHENTICATE_FAILED
    }

    /**
     * Application callback handler interface.
     */
    public interface Handler {
        /**
         * Callback for the "on enter" action.
         *
         * @param agent Handle to the agent instance
         */
        void onEnter(OnceServerAgent agent);

        /**
         * Callback for the "on join" action.
         *
         * @param agent Handle to the agent instance
         */
        void onJoin(OnceServerAgent agent);

        /**
         * Callback for the "check authentication token" action.
         *
         * @param agent Handle to the agent instance
         */
        void checkAuthenticationToken(OnceServerAgent agent);

        /**
         * Callback for the "on whisper" action.
         *
         * @param agent Handle to the agent instance
         */
        void onWhisper(OnceServerAgent agent);

        /**
         * Callback for the "on shout" action.
         *
         * @param agent Handle to the agent instance
         */
        void onShout(OnceServerAgent agent);

        /**
         * Callback for the "on leave" action.
         *
         * @param agent Handle to the agent instance
         */
        void onLeave(OnceServerAgent agent);

        /**
         * Callback for the "on exit" action.
         *
         * @param agent Handle to the agent instance
         */
        void onExit(OnceServerAgent agent);

        /**
         * Callback for the "on authenticate" action.
         *
         * @param agent Handle to the agent instance
         */
        void onAuthenticate(OnceServerAgent agent);

        /**
         * Callback for the "on unauthorized" action.
         *
         * @param agent Handle to the agent instance
         */
        void onUnauthorized(OnceServerAgent agent);

        /**
         * Callback for the "on get endpoints" action.
         *
         * @param agent Handle to the agent instance
         */
        void onGetEndpoints(OnceServerAgent agent);
    }
}
