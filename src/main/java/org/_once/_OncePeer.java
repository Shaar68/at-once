package org._once;

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
    private ZreInterface localZre;
    private ZreInterface remoteZre;

    private String server;
    private Map<String, String> bridgePeers = new HashMap<>();
    private Map<String, List<RemotePeer>> remotePeers = new HashMap<>();
    private Context context;

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
            .withInPollable(remoteZre.getSocket(), new RemoteHandler())
            .start();
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

    private class RemoteHandler extends LoopAdapter {
        @Override
        protected void execute(Reactor reactor, Socket socket) {
            Message message = remoteZre.receive();
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
            if (server != null) {
                String address = remoteZre.getPeerEndpoint(peer);
                bridgePeers.put(peer, address);

                Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
                frameBuilder.putString("GET PEERS");
                remoteZre.whisper(peer, new Message(frameBuilder.build()));
            }
        }

        private void onLeave(Message message) {
            String peer = message.popString();
            if (server != null) {
                bridgePeers.remove(peer);
                remotePeers.remove(peer);
            }
        }

        private void onWhisper(Message message) {
            String peer = message.popString();
            Message.Frame frame = message.popFrame();
            String command = frame.getChars();
            switch (command) {
                case "ICU":
                    onIcu(peer);
                    break;
                case "LIST ENDPOINTS":
                    onListEndpoints(peer, frame);
                    break;
                case "GET PEERS":
                    onGetPeers(peer);
                    break;
                case "LIST PEERS":
                    onListPeers(peer, frame);
                    break;
            }
        }

        private void onShout(Message message) {
            String peer = message.popString();
            Message.Frame frame = message.popFrame();
            String command = frame.getChars();
            switch (command) {
                case "ENTER":
                    onRemoteEnter(peer, frame);
                    break;
                case "LEAVE":
                    onRemoteLeave(peer, frame);
                    break;
            }
        }

        private void onRemoteEnter(String sender, Message.Frame frame) {
            String peer = frame.getChars();
            String name = frame.getChars();
            remotePeers.getOrDefault(sender, new ArrayList<>()).add(new RemotePeer(peer, name));
        }

        private void onRemoteLeave(String sender, Message.Frame frame) {
            List<RemotePeer> peers = remotePeers.get(sender);
            String peer = frame.getChars();
            String name = frame.getChars();
            if (peers != null) {
                peers.remove(new RemotePeer(peer, name));
            }
        }

        private void onIcu(String peer) {
            server = peer;
            System.out.printf("Connected to %s\n", remoteZre.getPeerName(server));

            Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
            frameBuilder.putString("GET ENDPOINTS");
            remoteZre.whisper(server, new Message(frameBuilder.build()));
        }

        private void onListEndpoints(String sender, Message.Frame frame) {
            Map<String, String> endpoints = frame.getMap();
            for (String address : bridgePeers.values()) {
                remoteZre.connect(address);
            }

            System.out.printf("Received list of peers from %s: size=%d\n", remoteZre.getPeerName(sender), endpoints.size());
        }

        private void onGetPeers(String replyTo) {
            List<String> peers = remoteZre.getPeers();
            Map<String, String> map = new HashMap<>(peers.size(), 1.0f);
            for (String peer : peers) {
                map.put(peer, remoteZre.getPeerName(peer));
            }

            Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
            frameBuilder.putString("LIST PEERS");
            frameBuilder.putMap(map);
            remoteZre.whisper(replyTo, new Message(frameBuilder.build()));
        }

        private void onListPeers(String peer, Message.Frame frame) {
            Map<String, String> peers = frame.getMap();
            List<RemotePeer> list = new ArrayList<>(peers.size());
            for (Map.Entry<String, String> entry : peers.entrySet()){
                list.add(new RemotePeer(entry.getKey(), entry.getValue()));
            }

            remotePeers.put(peer, list);
            System.out.printf("Received list of peers from %s: size=%d\n", remoteZre.getPeerName(peer), peers.size());
        }
    }
}
