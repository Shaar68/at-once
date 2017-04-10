package org._once;

import org.jyre.ZreInterface;
import org.zeromq.api.Context;
import org.zeromq.api.Message;
import org.zeromq.api.Socket;

public class _OnceClient extends ZreInterface {
    public _OnceClient() {
        super();
    }

    public _OnceClient(Context context) {
        super(context);
    }

    @Override
    public Socket getSocket() {
        return super.getSocket();
    }

    @Override
    public Message receiveMessage() {
        return super.receiveMessage();
    }
}
