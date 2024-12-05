package game.NetMessage;

import game.Material;
import game.World;

import java.nio.ByteBuffer;

public class ChunkSync extends Message {
    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new ChunkSync());}

    public static byte getId() {
        return id;
    }

    public byte[] buildMessage() {

        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);

        return message.array();
    }

    @Override
    public void processMessage(ByteBuffer message) {

    }
}
