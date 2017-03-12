/* ============================================================================
 * OnceClientAgent.java
 *
 * Generated class for OnceClientAgent
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
 * OnceClientAgent class.
 *
 * @author sriesenberg
 */
public class OnceClientAgent extends LoopAdapter {
    // Application callback handler
    private Handler handler;

    // Structure of our class
    private Reactor reactor;
    private Socket socket;
    private Message message;
    private State state = State.START;
    private Event event;
    private Event next;

    public OnceClientAgent(Handler handler) {
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
                        state = State.READY;
                        break;
                    }
                }
                break;
            }
            case READY: {
                switch (event) {
                    case BRIDGE_ENTERED: {
                        state = State.BRIDGE_MODE;
                        break;
                    }
                    case ENTER: {
                        handler.onEnter(this);
                        break;
                    }
                    case JOIN: {
                        handler.onJoin(this);
                        break;
                    }
                    case WHISPER: {
                        handler.onWhisper(this);
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
            case BRIDGE_MODE: {
                switch (event) {
                    case REMOTE_ENTER: {
                        handler.onRemoteEnter(this);
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
                    case REMOTE_EXIT: {
                        handler.onRemoteExit(this);
                        break;
                    }
                    case BRIDGE_LEFT: {
                        state = State.READY;
                        break;
                    }
                    case ENTER: {
                        handler.onEnter(this);
                        break;
                    }
                    case JOIN: {
                        handler.onJoin(this);
                        break;
                    }
                    case WHISPER: {
                        handler.onWhisper(this);
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
        }
    }

    /**
     * States we can be in.
     */
    public enum State {
        START,
        READY,
        BRIDGE_MODE
    }

    /**
     * Events we can process.
     */
    public enum Event {
        ENTER,
        BRIDGE_ENTERED,
        REMOTE_ENTER,
        REMOTE_WHISPER,
        REMOTE_SHOUT,
        REMOTE_EXIT,
        BRIDGE_LEFT,
        JOIN,
        WHISPER,
        SHOUT,
        LEAVE,
        EXIT
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
        void onEnter(OnceClientAgent agent);

        /**
         * Callback for the "on remote enter" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteEnter(OnceClientAgent agent);

        /**
         * Callback for the "on remote whisper" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteWhisper(OnceClientAgent agent);

        /**
         * Callback for the "on remote shout" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteShout(OnceClientAgent agent);

        /**
         * Callback for the "on remote exit" action.
         *
         * @param agent Handle to the agent instance
         */
        void onRemoteExit(OnceClientAgent agent);

        /**
         * Callback for the "on join" action.
         *
         * @param agent Handle to the agent instance
         */
        void onJoin(OnceClientAgent agent);

        /**
         * Callback for the "on whisper" action.
         *
         * @param agent Handle to the agent instance
         */
        void onWhisper(OnceClientAgent agent);

        /**
         * Callback for the "on shout" action.
         *
         * @param agent Handle to the agent instance
         */
        void onShout(OnceClientAgent agent);

        /**
         * Callback for the "on leave" action.
         *
         * @param agent Handle to the agent instance
         */
        void onLeave(OnceClientAgent agent);

        /**
         * Callback for the "on exit" action.
         *
         * @param agent Handle to the agent instance
         */
        void onExit(OnceClientAgent agent);
    }
}
