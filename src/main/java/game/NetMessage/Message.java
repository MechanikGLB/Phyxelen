package game.NetMessage;

import java.nio.ByteBuffer;

abstract public class Message {

    public abstract byte[] buildMessage();
    public abstract void processMessage(ByteBuffer message);
}


//    static final byte Hello = 1;
//    static final byte Quit = 2;
//    static final byte SendSubworldParams = 3;
//    static final byte CreateEntity = 4;
//    static final byte SendEntityPosition = 5;
//    static final byte SendChunk = 6;