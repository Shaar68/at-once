package org._once;

import org._once.agent.OnceServerAgent;
import org._once.protocol.ChallengeMessage;
import org._once.protocol.ListEndpointsMessage;
import org._once.protocol.NopeMessage;
import org._once.protocol.OkMessage;
import org._once.protocol.OnceCodec;
import org.jyre.ZreInterface;
import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Message;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class _OnceServer {
    private Context context;
    private ZreInterface zre;
    private OnceCodec codec = new OnceCodec();

    public static void main(String[] args) {
        new _OnceServer().run(args[0]);
    }

    private void run(String name) {
        context = ContextFactory.createContext(1);

        zre = new ZreInterface(context);
        zre.setName(name);
        zre.setBeaconsEnabled(false);
        zre.setPort(_OnceConstants.ZRE_PING_PORT + 1);
        zre.start();

        context.buildReactor()
            .withInPollable(zre.getSocket(), new OnceServerAgent(new ServerHandler()))
            .start();
    }

    private String time() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")).toLowerCase();
    }

    private class ServerHandler implements OnceServerAgent.Handler {
        private String peer;

        @Override
        public void onEnter(OnceServerAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            String name = message.popString();
            if (name != null) {
                System.out.printf("%s: %s entered\n", time(), name);
            }

            ChallengeMessage challenge = new ChallengeMessage()
                .withMechanism("SIMPLE")
                .withChallenge("SECRET".getBytes());
            zre.whisper(peer, codec.serialize(challenge));
        }

        @Override
        public void onJoin(OnceServerAgent agent) {
            // Do nothing.
        }

        @Override
        public void onWhisper(OnceServerAgent agent) {
            Message message = agent.getMessage();
            peer = message.popString();

            OnceCodec.MessageType messageType = codec.deserialize(message);
            switch (messageType) {
                case AUTHENTICATE:
                    agent.triggerEvent(OnceServerAgent.Event.AUTHENTICATE);
                    break;
                case GET_ENDPOINTS:
                    agent.triggerEvent(OnceServerAgent.Event.GET_ENDPOINTS);
                    break;
            }
        }

        @Override
        public void onShout(OnceServerAgent agent) {
            // Do nothing.
        }

        @Override
        public void onLeave(OnceServerAgent agent) {
            // Do nothing.
        }

        @Override
        public void onExit(OnceServerAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            String name = message.popString();
            if (name != null) {
                System.out.printf("%s: %s left\n", time(), name);
            }
        }

        @Override
        public void checkAuthenticationToken(OnceServerAgent agent) {
            // TODO: Implement security.
        }

        @Override
        public void onAuthenticate(OnceServerAgent agent) {
            // TODO: Implement security.
            agent.triggerEvent(OnceServerAgent.Event.AUTHENTICATE_OK);
            zre.whisper(peer, codec.serialize(new OkMessage()));
        }

        @Override
        public void onAuthenticateFailed(OnceServerAgent agent) {
            // TODO: Implement security.
        }

        @Override
        public void onUnauthorized(OnceServerAgent agent) {
            zre.whisper(peer, codec.serialize(new NopeMessage()));
        }

        @Override
        public void onGetEndpoints(OnceServerAgent agent) {
            List<String> peers = zre.getPeers();
            ListEndpointsMessage message = new ListEndpointsMessage();
            for (String peer : peers) {
                String endpoint = zre.getPeerEndpoint(peer);
                String address = endpoint.substring("tcp://".length(), endpoint.lastIndexOf(":"));
                message.withEndpoint(address);
            }

            zre.whisper(peer, codec.serialize(message));
        }
    }
}
