package org._once;

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
import org.zeromq.api.LoopAdapter;
import org.zeromq.api.Message;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;

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
        remoteZre.receive(); // ENTER

        context.buildReactor()
            .withInPollable(localZre.getSocket(), new LocalHandler())
            .withInPollable(remoteZre.getSocket(), new OncePeerRemoteAgent(new RemoteHandler()))
            .start();
    }

    private class RemoteHandler implements OncePeerRemoteAgent.Handler {
        private String peer;
        private String group;
        private OnceCodec.MessageType messageType;

        @Override
        public void onWhisper(OncePeerRemoteAgent handle) {
            Message message = handle.getMessage();
            peer = message.popString();
            messageType = codec.deserialize(message);

            onMessage(handle);
        }

        @Override
        public void onShout(OncePeerRemoteAgent handle) {
            Message message = handle.getMessage();
            peer = message.popString();
            group = message.popString();

            onMessage(handle);
        }

        private void onMessage(OncePeerRemoteAgent handle) {
            switch (messageType) {
                case CHALLENGE:
                    handle.triggerEvent(Event.CHALLENGE);
                    break;
                case OK:
                    handle.triggerEvent(Event.OK);
                    break;
                case NOPE:
                    handle.triggerEvent(Event.NOPE);
                    break;
                case LIST_ENDPOINTS:
                    handle.triggerEvent(Event.LIST_ENDPOINTS);
                    break;
                case GET_PEERS:
                    handle.triggerEvent(Event.GET_PEERS);
                    break;
                case LIST_PEERS:
                    handle.triggerEvent(Event.LIST_PEERS);
                    break;
                case REMOTE_ENTER:
                    handle.triggerEvent(Event.REMOTE_ENTER);
                    break;
                case REMOTE_EXIT:
                    handle.triggerEvent(Event.REMOTE_EXIT);
                    break;
                case STOP:
                    handle.triggerEvent(Event.STOP);
                    break;
            }
        }

        @Override
        public void onServerConnect(OncePeerRemoteAgent handle) {
            Message message = handle.getMessage();
            server = message.popString();
        }

        @Override
        public void onChallenge(OncePeerRemoteAgent handle) {
            // TODO: Handle challenge
            handle.triggerEvent(Event.OK);
        }

        @Override
        public void onOk(OncePeerRemoteAgent handle) {
            GetEndpointsMessage message = new GetEndpointsMessage();
            remoteZre.whisper(server, codec.serialize(message));
        }

        @Override
        public void onListEndpoints(OncePeerRemoteAgent handle) {
            OnceCodec.MessageType messageType = codec.deserialize(handle.getMessage());
            assert (messageType == OnceCodec.MessageType.LIST_ENDPOINTS);

            ListEndpointsMessage message = codec.getListEndpoints();
            List<String> endpoints = message.getEndpoints();
            for (String endpoint : endpoints) {
                remoteZre.connect(endpoint);
            }

            System.out.printf("Received list of peers from %s: size=%d\n", remoteZre.getPeerName(peer), endpoints.size());
        }

        @Override
        public void onEnter(OncePeerRemoteAgent handle) {
            Message message = handle.getMessage();
            String peer = message.popString();
            String endpoint = remoteZre.getPeerEndpoint(peer);
            bridgePeers.put(peer, endpoint);

            remoteZre.whisper(peer, codec.serialize(new GetPeersMessage()));
        }

        @Override
        public void onLeave(OncePeerRemoteAgent handle) {
            Message message = handle.getMessage();
            String peer = message.popString();
            bridgePeers.remove(peer);
            remotePeers.remove(peer);
        }

        @Override
        public void onGetPeers(OncePeerRemoteAgent handle) {
            List<String> peers = remoteZre.getPeers();
            ListPeersMessage message = new ListPeersMessage();
            for (String peer : peers) {
                message.withPeer(peer, remoteZre.getPeerName(peer));
            }

            remoteZre.whisper(peer, codec.serialize(message));
        }

        @Override
        public void onListPeers(OncePeerRemoteAgent handle) {
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
        public void onRemoteEnter(OncePeerRemoteAgent handle) {
            List<RemotePeer> peers = remotePeers.computeIfAbsent(peer, k -> new ArrayList<>());
            RemoteEnterMessage message = codec.getRemoteEnter();
            peers.add(new RemotePeer(message.getPeer(), message.getName()));
        }

        @Override
        public void onRemoteExit(OncePeerRemoteAgent handle) {
            List<RemotePeer> peers = remotePeers.get(peer);
            if (peers != null) {
                RemoteExitMessage message = codec.getRemoteExit();
                peers.remove(new RemotePeer(message.getPeer(), message.getName()));
            }
        }

        @Override
        public void onRemoteWhisper(OncePeerRemoteAgent handle) {
            // TODO: Implement remote whisper
        }

        @Override
        public void onRemoteShout(OncePeerRemoteAgent handle) {
            // TODO: Implement remote shout
        }

        @Override
        public void execute(OncePeerRemoteAgent handle) {
            // TODO: Needed?
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

    private class LocalHandler extends LoopAdapter {
        @Override
        protected void execute(Reactor reactor, Socket socket) {
            Message message = localZre.receive();
            String command = message.popString();
            switch (command) {
                case "ENTER":
                    onEnter(message);
                    break;
                case "LEAVE":
                    onLeave(message);
                    break;
                case "WHISPER":
                    onWhisper(message);
                    break;
                case "SHOUT":
                    onShout(message);
                    break;
            }
        }

        private void onEnter(Message message) {
            String peer = message.popString();
            String name = message.popString();

            Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
            frameBuilder.putString("ENTER");
            frameBuilder.putString(peer);
            frameBuilder.putString(name);
            remoteZre.shout("everyone", new Message(frameBuilder.build()));
        }

        private void onLeave(Message message) {
            String peer = message.popString();
            String name = message.popString();

            Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
            frameBuilder.putString("LEAVE");
            frameBuilder.putString(peer);
            frameBuilder.putString(name);
            remoteZre.shout("everyone", new Message(frameBuilder.build()));
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
