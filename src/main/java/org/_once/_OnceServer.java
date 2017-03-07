package org._once;

import org.jyre.ZreInterface;
import org.zeromq.api.Message;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _OnceServer {
    private ZreInterface zre;

    public static void main(String[] args) {
        new _OnceServer().run(args[0]);
    }

    private void run(String name) {
        zre = new ZreInterface();
        zre.setName(name);
        zre.setBeaconsEnabled(false);
        zre.setPort(_OnceConstants.ZRE_PING_PORT + 1);
        zre.start();
        while (true) {
            Message message = zre.receive();
            String command = message.popString();
            switch (command) {
                case "ENTER":
                    onEnter(message);
                    break;
                case "EXIT":
                    onExit(message);
                    break;
                case "JOIN":
                    onJoin(message);
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
                case "EVASIVE":
                    onEvasive(message);
                    break;
            }
        }
    }

    private void onEnter(Message message) {
        String peer = message.popString();
        String name = zre.getPeerName(peer);
        if (name != null) {
            System.out.printf("%s: %s entered\n", time(), name);
        }

        Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
        frameBuilder.putString("ICU");
        zre.whisper(peer, new Message(frameBuilder.build()));
    }

    private void onExit(Message message) {
        message.popString();
        String name = message.popString();
        if (name != null) {
            System.out.printf("%s: %s left\n", time(), name);
        }
    }

    private void onJoin(Message message) {
        String peer = message.popString();
        String group = message.popString();
        String name = zre.getPeerName(peer);
        if (name != null) {
            System.out.printf("%s: %s joined %s\n", time(), name, group);
        }
    }

    private void onLeave(Message message) {
        String peer = message.popString();
        String group = message.popString();
        String name = zre.getPeerName(peer);
        if (name != null) {
            System.out.printf("%s: %s left %s\n", time(), name, group);
        }
    }

    private void onWhisper(Message message) {
        String peer = message.popString();
        Message.Frame frame = message.popFrame();
        String command = frame.getChars();
        switch (command) {
            case "GET ENDPOINTS":
                onGetEndpoints(peer);
                break;
        }
    }

    private void onGetEndpoints(String replyTo) {
        List<String> peers = zre.getPeers();
        Map<String, String> endpoints = new HashMap<>(peers.size(), 1.0f);
        for (String peer : peers) {
            String endpoint = zre.getPeerEndpoint(peer);
            String address = endpoint.substring("tcp://".length(), endpoint.lastIndexOf(":"));
            endpoints.put(peer, address);
        }

        Message.FrameBuilder frameBuilder = new Message.FrameBuilder();
        frameBuilder.putString("LIST ENDPOINTS");
        frameBuilder.putMap(endpoints);
        zre.whisper(replyTo, new Message(frameBuilder.build()));
    }

    private void onShout(Message message) {
        String peer = message.popString();
        String group = message.popString();
        String content = message.popString();
        System.out.printf("%s: #%-12s @%-20s %s\n", time(), group, zre.getPeerName(peer), content);
    }

    private void onEvasive(Message message) {
        // Do nothing.
    }

    private String time() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")).toLowerCase();
    }
}
