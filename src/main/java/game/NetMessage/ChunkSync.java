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

    public ChunkSync(Chunk chunk) {
        this.chunk = chunk;
    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2 + Chunk.area());
//        System.out.println("Sends chunk "+chunk.getXIndex()+" ; "+chunk.getYIndex());
        message.put(id);
        message.putInt(chunk.getXIndex());
        message.putInt(chunk.getYIndex());
        for (int i = 0; i < Chunk.area(); i++) {
            message.put((byte)(
                    chunk.getPixelMaterial(i).getId() + (chunk.getPixelColor(i) << 5)
            ));
        }

        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        var subworld = Main.getGame().getActiveSubworld();
//        message.get();
        int xIndex = message.getInt();
        int yIndex = message.getInt();
        System.out.println("Received chunk "+xIndex+" ; "+yIndex);
        Material[] materials = new Material[Chunk.area()];
        byte[] colors = new byte[Chunk.area()];
        int air = 0;
        for (int i = 0; i < Chunk.area(); i++) {
            byte pixel = message.get();
            if (pixel == 0) air++;
            materials[i] = subworld.world().getMaterialById(pixel & 0x1F);
            colors[i] = (byte)(pixel >> 5);
        }
        Chunk receivedChunk = new Chunk(subworld, xIndex, yIndex, materials, colors);
        subworld.receivedChunk(receivedChunk);
    }
}
