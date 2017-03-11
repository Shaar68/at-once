package org._once;

import org._once.agent.OncePeerLocalAgent;
import org._once.agent.OncePeerRemoteAgent;
import org._once.agent.OncePeerRemoteAgent.Event;
import org._once.protocol.GetEndpointsMessage;
import org._once.protocol.GetPeersMessage;
import org._once.protocol.ListEndpointsMessage;
import org._once.protocol.ListPeersMessage;
import org._once.protocol.OnceCodec;
import org._once.protocol.RemoteEnterMessage;
import org._once.protocol.RemoteExitMessage;
import org.jyre.ZreInterface;
import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _OncePeer {
    private Context context;
    private ZreInterface localZre;
    private ZreInterface remoteZre;
    private OnceCodec codec = new OnceCodec();

    private String server;
    private Map<String, String> bridgePeers = new HashMap<>();
    private Map<String, List<RemotePeer>> remotePeers = new HashMap<>();

    public static void main(String[] args) {
        new _OncePeer().run(args[0]);
    }

    private void run(String name) {
        context = ContextFactory.createContext(1);

        localZre = new ZreInterface(context);
        localZre.setName(name);
        localZre.setHeader("X-Bridge-Peer", "true");
        localZre.start();

        remoteZre = new ZreInterface(context);
        remoteZre.setName(name);
        remoteZre.setBeaconsEnabled(false);
        remoteZre.setPort(_OnceConstants.ZRE_PING_PORT + 1);
        remoteZre.start();
        remoteZre.connect("jyre.org");
        remoteZre.join("everyone");

        context.buildReactor()
            .withInPollable(localZre.getSocket(), new OncePeerLocalAgent(new LocalHandler()))
            .withInPollable(remoteZre.getSocket(), new OncePeerRemoteAgent(new RemoteHandler()))
            .start();
    }

    private class RemoteHandler implements OncePeerRemoteAgent.Handler {
        private String peer;
        private String group;
        private OnceCodec.MessageType messageType;

        @Override
        public void onEnter(OncePeerRemoteAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            String endpoint = remoteZre.getPeerEndpoint(peer);
            bridgePeers.put(peer, endpoint);

            remoteZre.whisper(peer, codec.serialize(new GetPeersMessage()));
        }

        @Override
        public void onJoin(OncePeerRemoteAgent agent) {
            // Do nothing.
        }

        @Override
        public void onLeave(OncePeerRemoteAgent agent) {
            // Do nothing.
        }

        @Override
        public void onExit(OncePeerRemoteAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            bridgePeers.remove(peer);
            remotePeers.remove(peer);
        }

        @Override
        public void checkSharedSecret(OncePeerRemoteAgent agent) {
            // TODO: Implement security.
        }

        @Override
        public void onWhisper(OncePeerRemoteAgent agent) {
            Message message = agent.getMessage();
            peer = message.popString();
            messageType = codec.deserialize(message);

            onMessage(agent);
        }

        @Override
        public void onShout(OncePeerRemoteAgent agent) {
            Message message = agent.getMessage();
            peer = message.popString();
            group = message.popString();
            messageType = codec.deserialize(message);

            onMessage(agent);
        }

        private void onMessage(OncePeerRemoteAgent agent) {
            switch (messageType) {
                case CHALLENGE:
                    agent.triggerEvent(Event.CHALLENGE);
                    break;
                case OK:
                    agent.triggerEvent(Event.OK);
                    break;
                case NOPE:
                    agent.triggerEvent(Event.NOPE);
                    break;
                case LIST_ENDPOINTS:
                    agent.triggerEvent(Event.LIST_ENDPOINTS);
                    break;
                case GET_PEERS:
                    agent.triggerEvent(Event.GET_PEERS);
                    break;
                case LIST_PEERS:
                    agent.triggerEvent(Event.LIST_PEERS);
                    break;
                case REMOTE_ENTER:
                    agent.triggerEvent(Event.REMOTE_ENTER);
                    break;
                case REMOTE_EXIT:
                    agent.triggerEvent(Event.REMOTE_EXIT);
                    break;
                case STOP:
                    agent.triggerEvent(Event.STOP);
                    break;
            }
        }

        @Override
        public void onServerConnect(OncePeerRemoteAgent agent) {
            Message message = agent.getMessage();
            server = message.popString();
        }

        @Override
        public void onChallenge(OncePeerRemoteAgent agent) {
            // TODO: Handle challenge
            agent.triggerEvent(Event.OK);
        }

        @Override
        public void onOk(OncePeerRemoteAgent agent) {
            GetEndpointsMessage message = new GetEndpointsMessage();
            remoteZre.whisper(server, codec.serialize(message));
        }

        @Override
        public void onListEndpoints(OncePeerRemoteAgent agent) {
            OnceCodec.MessageType messageType = codec.deserialize(agent.getMessage());
            assert (messageType == OnceCodec.MessageType.LIST_ENDPOINTS);

            ListEndpointsMessage message = codec.getListEndpoints();
            List<String> endpoints = message.getEndpoints();
            for (String endpoint : endpoints) {
                remoteZre.connect(endpoint);
            }

            System.out.printf("Received list of peers from %s: size=%d\n", remoteZre.getPeerName(peer), endpoints.size());
        }

        @Override
        public void onGetPeers(OncePeerRemoteAgent agent) {
            List<String> peers = remoteZre.getPeers();
            ListPeersMessage message = new ListPeersMessage();
            for (String peer : peers) {
                message.withPeer(peer, remoteZre.getPeerName(peer));
            }

            remoteZre.whisper(peer, codec.serialize(message));
        }

        @Override
        public void onListPeers(OncePeerRemoteAgent agent) {
            ListPeersMessage message = codec.getListPeers();
            Map<String, String> peers = message.getPeers();

            List<RemotePeer> list = new ArrayList<>(peers.size());
            for (Map.Entry<String, String> entry : peers.entrySet()) {
                list.add(new RemotePeer(entry.getKey(), entry.getValue()));
            }

            remotePeers.put(peer, list);
            System.out.printf("Received list of peers from %s: size=%d\n", remoteZre.getPeerName(peer), peers.size());
        }

        @Override
        public void onRemoteEnter(OncePeerRemoteAgent agent) {
            List<RemotePeer> peers = remotePeers.computeIfAbsent(peer, k -> new ArrayList<>());
            RemoteEnterMessage message = codec.getRemoteEnter();
            peers.add(new RemotePeer(message.getPeer(), message.getName()));
        }

        @Override
        public void onRemoteExit(OncePeerRemoteAgent agent) {
            List<RemotePeer> peers = remotePeers.get(peer);
            if (peers != null) {
                RemoteExitMessage message = codec.getRemoteExit();
                peers.remove(new RemotePeer(message.getPeer(), message.getName()));
            }
        }

        @Override
        public void onRemoteWhisper(OncePeerRemoteAgent agent) {
            // TODO: Implement remote whisper
        }

        @Override
        public void onRemoteShout(OncePeerRemoteAgent agent) {
            // TODO: Implement remote shout
        }
    }

    private class RemotePeer {
        private final String identity;
        private final String name;

        public RemotePeer(String identity, String name) {
            this.identity = identity;
            this.name = name;
        }

        public String getIdentity() {
            return identity;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return identity.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                && obj instanceof RemotePeer
                && ((RemotePeer) obj).identity.equals(identity);
        }
    }

    private class LocalHandler implements OncePeerLocalAgent.Handler {
        @Override
        public void onEnter(OncePeerLocalAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            String name = message.popString();

            RemoteEnterMessage enter = new RemoteEnterMessage()
                .withPeer(peer)
                .withName(name);
            remoteZre.shout("everyone", codec.serialize(enter));
        }

        @Override
        public void onJoin(OncePeerLocalAgent agent) {

        }

        @Override
        public void onWhisper(OncePeerLocalAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();

            OnceCodec.MessageType messageType = codec.deserialize(message);
            switch (messageType) {
                case REMOTE_WHISPER:
                    // TODO: Implement remote whisper
                    break;
                case REMOTE_SHOUT:
                    // TODO: Implement remote shout
                    break;
            }
        }

        @Override
        public void onShout(OncePeerLocalAgent agent) {
            // Do nothing.
        }

        @Override
        public void onLeave(OncePeerLocalAgent agent) {
            // Do nothing.
        }

        @Override
        public void onExit(OncePeerLocalAgent agent) {
            Message message = agent.getMessage();
            String peer = message.popString();
            String name = message.popString();

            RemoteExitMessage exit = new RemoteExitMessage()
                .withPeer(peer)
                .withName(name);
            remoteZre.shout("everyone", codec.serialize(exit));
        }

        private void onWhisper(Message message) {
            Message.Frame frame = message.popFrame();
            String peer = frame.getChars();
            String content = frame.getClob();
            remoteZre.whisper(peer, new Message(content));
        }

        private void onShout(Message message) {
            Message.Frame frame = message.popFrame();
            String group = frame.getChars();
            String content = frame.getClob();
            remoteZre.shout(group, new Message(content));
        }
    }
}
