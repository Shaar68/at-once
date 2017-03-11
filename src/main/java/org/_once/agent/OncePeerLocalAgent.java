package org._once.agent;

import org._once.protocol.OnceCodec;
import org.jyre.ZreInterface;

public class OncePeerLocalAgent {
    private ZreInterface localZre;
    private ZreInterface remoteZre;

    private OnceCodec codec = new OnceCodec();
}
