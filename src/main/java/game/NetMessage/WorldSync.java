package game.NetMessage;

import java.nio.ByteBuffer;

public class WorldSync extends Message {
    static byte id = Messages.getNextMessageIndex();

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        //TODO:World SYNC with server
        return message.array();
    }
}
