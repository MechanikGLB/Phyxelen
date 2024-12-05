package game.NetMessage;

import java.nio.ByteBuffer;

public class SendInt extends Message{
    static byte id = Messages.getNextMessageIndex();
    static int intToSend;

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1+Integer.BYTES);
        message.put(id);
        message.putInt(intToSend);
        return message.array();
    }
}
