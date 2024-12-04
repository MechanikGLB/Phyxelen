package game.NetMessage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Hello extends Message {
    static byte id = Messages.getNextMessageIndex();

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }
}