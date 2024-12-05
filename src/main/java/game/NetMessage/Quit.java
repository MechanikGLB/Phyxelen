package game.NetMessage;

import java.nio.ByteBuffer;

public class Quit extends Message {
    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new Quit());}

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }

    @Override
    public void processMessage(ByteBuffer message) {

    }
}
