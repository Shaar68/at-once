package org._once.protocol;

import org.junit.Before;
import org.junit.Test;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test OnceSocket.
 */
public class OnceSocketTest {
    private Context context;
    private Socket dealer;
    private Socket router;
    
    @Before
    public void setUp() {
        context = new ManagedContext();
        dealer = context.buildSocket(SocketType.DEALER)
            .bind("inproc://selftest");
        router = context.buildSocket(SocketType.ROUTER)
            .connect("inproc://selftest");
    }

    @Test
    public void testChallenge() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        ChallengeMessage message = new ChallengeMessage();
        message.addMechanism("Name: Brutus");
        message.addMechanism("Age: 43");
        byte[] challengeData = new byte[2];
        challengeData[0] = (byte) 0x7f;
        challengeData[1] = (byte) 0x7c;
        message.setChallenge(challengeData);
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.CHALLENGE, in.receive());
        message = in.getChallenge();
        assertEquals(message.getMechanisms().size(), 2);
        assertEquals(message.getMechanisms().get(0), "Name: Brutus");
        assertEquals(message.getMechanisms().get(1), "Age: 43");
        assertEquals(message.getChallenge()[0], 0x7f);
        assertEquals(message.getChallenge()[1], 0x7c);
        
        out.close();
        in.close();
    }

    @Test
    public void testAuthenticate() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        AuthenticateMessage message = new AuthenticateMessage();
        message.setMechanism("Life is short but Now lasts for ever");
        byte[] responseData = new byte[2];
        responseData[0] = (byte) 0x7f;
        responseData[1] = (byte) 0x7c;
        message.setResponse(responseData);
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.AUTHENTICATE, in.receive());
        message = in.getAuthenticate();
        assertEquals(message.getMechanism(), "Life is short but Now lasts for ever");
        assertEquals(message.getResponse()[0], 0x7f);
        assertEquals(message.getResponse()[1], 0x7c);
        
        out.close();
        in.close();
    }

    @Test
    public void testOk() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        OkMessage message = new OkMessage();
        message.setToken("Life is short but Now lasts for ever");
        message.setSecret("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.OK, in.receive());
        message = in.getOk();
        assertEquals(message.getToken(), "Life is short but Now lasts for ever");
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testNope() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        NopeMessage message = new NopeMessage();
        message.setStatusCode(123);
        message.setStatusText("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.NOPE, in.receive());
        message = in.getNope();
        assertEquals(message.getStatusCode(), Integer.valueOf(123));
        assertEquals(message.getStatusText(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testGetEndpoints() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        GetEndpointsMessage message = new GetEndpointsMessage();
        message.setToken("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.GET_ENDPOINTS, in.receive());
        message = in.getGetEndpoints();
        assertEquals(message.getToken(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testListEndpoints() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        ListEndpointsMessage message = new ListEndpointsMessage();
        message.setToken("Life is short but Now lasts for ever");
        message.addEndpoint("Name: Brutus");
        message.addEndpoint("Age: 43");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.LIST_ENDPOINTS, in.receive());
        message = in.getListEndpoints();
        assertEquals(message.getToken(), "Life is short but Now lasts for ever");
        assertEquals(message.getEndpoints().size(), 2);
        assertEquals(message.getEndpoints().get(0), "Name: Brutus");
        assertEquals(message.getEndpoints().get(1), "Age: 43");
        
        out.close();
        in.close();
    }

    @Test
    public void testGetPeers() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        GetPeersMessage message = new GetPeersMessage();
        message.setSecret("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.GET_PEERS, in.receive());
        message = in.getGetPeers();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testListPeers() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        ListPeersMessage message = new ListPeersMessage();
        message.setSecret("Life is short but Now lasts for ever");
        message.putPeer("Name", "Brutus");
        message.putPeer("Age", 43);
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.LIST_PEERS, in.receive());
        message = in.getListPeers();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        assertEquals(message.getPeers().size(), 2);
        assertEquals(message.getPeer("Name", "?"), "Brutus");
        assertEquals(message.getPeer("Age", 0), 43);
        
        out.close();
        in.close();
    }

    @Test
    public void testRemoteWhisper() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        RemoteWhisperMessage message = new RemoteWhisperMessage();
        message.setSecret("Life is short but Now lasts for ever");
        message.setFrom("Life is short but Now lasts for ever");
        message.setPeer("Life is short but Now lasts for ever");
        message.setContent("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_WHISPER, in.receive());
        message = in.getRemoteWhisper();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        assertEquals(message.getFrom(), "Life is short but Now lasts for ever");
        assertEquals(message.getPeer(), "Life is short but Now lasts for ever");
        assertEquals(message.getContent(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testRemoteShout() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        RemoteShoutMessage message = new RemoteShoutMessage();
        message.setSecret("Life is short but Now lasts for ever");
        message.setFrom("Life is short but Now lasts for ever");
        message.setGroup("Life is short but Now lasts for ever");
        message.setContent("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_SHOUT, in.receive());
        message = in.getRemoteShout();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        assertEquals(message.getFrom(), "Life is short but Now lasts for ever");
        assertEquals(message.getGroup(), "Life is short but Now lasts for ever");
        assertEquals(message.getContent(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testRemoteEnter() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        RemoteEnterMessage message = new RemoteEnterMessage();
        message.setSecret("Life is short but Now lasts for ever");
        message.setPeer("Life is short but Now lasts for ever");
        message.setName("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_ENTER, in.receive());
        message = in.getRemoteEnter();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        assertEquals(message.getPeer(), "Life is short but Now lasts for ever");
        assertEquals(message.getName(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testRemoteExit() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        RemoteExitMessage message = new RemoteExitMessage();
        message.setSecret("Life is short but Now lasts for ever");
        message.setPeer("Life is short but Now lasts for ever");
        message.setName("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_EXIT, in.receive());
        message = in.getRemoteExit();
        assertEquals(message.getSecret(), "Life is short but Now lasts for ever");
        assertEquals(message.getPeer(), "Life is short but Now lasts for ever");
        assertEquals(message.getName(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }

    @Test
    public void testStop() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        StopMessage message = new StopMessage();
        message.setToken("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.STOP, in.receive());
        message = in.getStop();
        assertEquals(message.getToken(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }
}