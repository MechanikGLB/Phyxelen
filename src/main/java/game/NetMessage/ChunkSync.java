package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class ChunkSync extends Message {
    static byte id = 5;

    Chunk chunk;

    public static byte getId() {
        return id;
    }

    public ChunkSync(Chunk chunk) {
        this.chunk = chunk;
    }

    public ChunkSync(ByteBuffer message) {
        var subworld = Main.getGame().getActiveSubworld();
        int xIndex = message.getInt();
        int yIndex = message.getInt();
        byte type = message.get();
        System.out.println("Received chunk "+xIndex+" ; "+yIndex);
        Material[] materials = new Material[Chunk.area()];
        byte[] colors = new byte[Chunk.area()];

        switch (type) {
            case 0: {
                for (int i = 0; i < Chunk.area(); i++) {
                    materials[i] = Content.air();
                    colors[i] = 0;
                }
            } break;
            case 1: {
                Material material = subworld.world().getMaterialById(message.get());
                for (int i = 0; i < Chunk.area(); i += 2) {
                    byte colorsByte = message.get();
                    materials[i] = material;
                    colors[i] = (byte)(colorsByte & 0xF);
                    materials[i+1] = material;
                    colors[i+1] = (byte)(colorsByte >> 4);
                }

            } break;
            case 2: {
                for (int i = 0; i < Chunk.area(); i++) {
                    byte pixel = message.get();
                    materials[i] = subworld.world().getMaterialById(pixel & 0x1F);
                    colors[i] = (byte)(pixel >> 5);
                }
            }
        }

        chunk = new Chunk(subworld, xIndex, yIndex, materials, colors);

    };

    public byte[] toBytes() {
        // Compressing type definition
        byte type = 0;
        Material materialA = chunk.getPixelMaterial(0);
        Material materialB;
        for (int i = 1; i < Chunk.area(); i++) {
            materialB = chunk.getPixelMaterial(i);
            if (materialA != materialB) {
                type = 2;
                break;
            }
        }
        if (type == 0 && materialA != Content.air())
            type = 1;

        // Sending
        ByteBuffer message = switch (type) {
            case 0 -> ByteBuffer.allocate(2 + Integer.BYTES * 2);
            case 1 -> ByteBuffer.allocate(3 + Integer.BYTES * 2 + Chunk.area() / 2);
            default -> ByteBuffer.allocate(2 + Integer.BYTES * 2 + Chunk.area());
        };
        //        System.out.println("Sends chunk "+chunk.getXIndex()+" ; "+chunk.getYIndex());
        message.put(id);
        message.putInt(chunk.getXIndex());
        message.putInt(chunk.getYIndex());


        message.put(type);
        switch (type) {
            case 1: {
                message.put(chunk.getPixelMaterial(0).getId());
                for (int i = 0; i < Chunk.area(); i += 2) {
                    message.put((byte)(
                            chunk.getPixelColor(i) + (chunk.getPixelColor(i + 1) << 4)
                    ));
                }
            } break;
            case 2: {
                for (int i = 0; i < Chunk.area(); i++) {
                    message.put((byte)(
                            chunk.getPixelMaterial(i).getId() + (chunk.getPixelColor(i) << 5)
                    ));
                }
            } break;
        }

        return message.array();
    }

    @Override
    public void process() {
        var subworld = Main.getGame().getActiveSubworld();
        subworld.receivedChunk(chunk);
    }
}
