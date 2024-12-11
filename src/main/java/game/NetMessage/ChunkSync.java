package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class ChunkSync extends Message {
    static byte id = 5;
//    static {Messages.addMessage(new ChunkSync());}
    Chunk chunk;

    public static byte getId() {
        return id;
    }

    ChunkSync() {};

    public ChunkSync(int x, int y) {
        var indexes = new VectorI(x, y);
        var subworld = Main.getGame().getActiveSubworld();
        subworld.loadChunk(indexes);
        chunk = subworld.getActiveChunk(indexes);
    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2 + Chunk.area());
        message.put(id);
        message.putInt(chunk.getXIndex());
        message.putInt(chunk.getYIndex());
        for (int i = 0; i < Chunk.area(); i++) {
            message.put((byte)(
                    chunk.getPixelMaterial(i).getId() +
                    chunk.getPixelColor(i) << 5
            ));
        }

        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        var subworld = Main.getGame().getActiveSubworld();
        int xIndex = message.getInt();
        int yIndex = message.getInt();
        Material[] materials = new Material[Chunk.area()];
        byte[] colors = new byte[Chunk.area()];
        for (int i = 0; i < Chunk.area(); i++) {
            byte pixel = message.get();
            materials[i] = subworld.world().getMaterialById(pixel & 0x1F);
            colors[i] = (byte)(pixel >> 5);
        }
        Chunk receivedChunk = new Chunk(subworld, materials, colors);
        subworld.loadedChunk(receivedChunk);
    }
}
