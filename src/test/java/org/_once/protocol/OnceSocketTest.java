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
    public void testGetEndpoints() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        GetEndpointsMessage message = new GetEndpointsMessage();
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.GET_ENDPOINTS, in.receive());
        message = in.getGetEndpoints();
        
        out.close();
        in.close();
    }

    @Test
    public void testListEndpoints() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        ListEndpointsMessage message = new ListEndpointsMessage();
        message.addPeer("Name: Brutus");
        message.addPeer("Age: 43");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.LIST_ENDPOINTS, in.receive());
        message = in.getListEndpoints();
        assertEquals(message.getPeers().size(), 2);
        assertEquals(message.getPeers().get(0), "Name: Brutus");
        assertEquals(message.getPeers().get(1), "Age: 43");
        
        out.close();
        in.close();
    }

    @Test
    public void testRemoteWhisper() {
        OnceSocket out = new OnceSocket(dealer);
        OnceSocket in = new OnceSocket(router);
        
        RemoteWhisperMessage message = new RemoteWhisperMessage();
        message.setPeer("Life is short but Now lasts for ever");
        message.setContent("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_WHISPER, in.receive());
        message = in.getRemoteWhisper();
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
        message.setGroup("Life is short but Now lasts for ever");
        message.setContent("Life is short but Now lasts for ever");
        
        assertTrue(out.send(message));
        assertEquals(OnceSocket.MessageType.REMOTE_SHOUT, in.receive());
        message = in.getRemoteShout();
        assertEquals(message.getGroup(), "Life is short but Now lasts for ever");
        assertEquals(message.getContent(), "Life is short but Now lasts for ever");
        
        out.close();
        in.close();
    }
}