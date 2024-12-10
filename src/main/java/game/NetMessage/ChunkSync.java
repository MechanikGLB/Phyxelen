package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class ChunkSync extends Message {
    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new ChunkSync());}
    Chunk chunk;

    public static byte getId() {
        return id;
    }

    private ChunkSync() {};

    public ChunkSync(int x, int y) {
        var indexes = new VectorI(x, y);
        var subworld = Main.getGame().getActiveSubworld();
        subworld.loadChunk(indexes);
        chunk = subworld.getActiveChunk(indexes);
    }

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1 + Chunk.area());
        message.put(id);
        for (int i = 0; i < Chunk.area(); i++) {
//            message.put(Pixels.make())
        }

        return message.array();
    }

    @Override
    public void processMessage(ByteBuffer message) {

    }
}
