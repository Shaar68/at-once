package org._once.agent;

import org.zeromq.api.LoopAdapter;
import org.zeromq.api.Message;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;

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
                    case WHISPER: {
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.onShout(this);
                        break;
                    }
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
                    case WHISPER: {
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.onShout(this);
                        break;
                    }
                    case CHALLENGE: {
                        handler.onChallenge(this);
                        break;
                    }
                    case OK: {
                        state = State.CONNECTING;
                        break;
                    }
                    case NOPE: {
                        reactor.cancel(this);
                        break;
                    }
                }
                break;
            }
            case CONNECTING: {
                switch (event) {
                    case WHISPER: {
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.onShout(this);
                        break;
                    }
                    case LIST_ENDPOINTS: {
                        handler.onListEndpoints(this);
                        state = State.READY;
                        break;
                    }
                }
                break;
            }
            case READY: {
                switch (event) {
                    case WHISPER: {
                        handler.onWhisper(this);
                        break;
                    }
                    case SHOUT: {
                        handler.onShout(this);
                        break;
                    }
                    case ENTER: {
                        handler.onEnter(this);
                        break;
                    }
                    case LEAVE: {
                        handler.onLeave(this);
                        break;
                    }
                    case GET_PEERS: {
                        handler.onGetPeers(this);
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
                    default: {
                        handler.execute(this);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     *
     */
    public enum State {
        START,
        AUTHENTICATING,
        CONNECTING,
        READY
    }

    /**
     *
     */
    public enum Event {
        WHISPER,
        SHOUT,
        ENTER,
        CHALLENGE,
        OK,
        NOPE,
        LIST_ENDPOINTS,
        LEAVE,
        GET_PEERS,
        LIST_PEERS,
        REMOTE_ENTER,
        REMOTE_EXIT,
        REMOTE_WHISPER,
        REMOTE_SHOUT,
        STOP
    }

    /**
     *
     */
    public interface Handler {
        /**
         *
         * @param handle
         */
        void onWhisper(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onShout(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onServerConnect(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onChallenge(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onOk(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onListEndpoints(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onEnter(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onGetPeers(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onListPeers(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onRemoteEnter(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onRemoteExit(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onRemoteWhisper(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onRemoteShout(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void onLeave(OncePeerRemoteAgent handle);

        /**
         *
         * @param handle
         */
        void execute(OncePeerRemoteAgent handle);
    }
}
