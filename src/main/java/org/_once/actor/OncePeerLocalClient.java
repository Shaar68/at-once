package org._once.actor;

import org._once.protocol.OnceCodec;
import org.jyre.ZreInterface;

public class OncePeerLocalClient {
    private ZreInterface localZre;
    private ZreInterface remoteZre;

    private OnceCodec codec = new OnceCodec();
}
