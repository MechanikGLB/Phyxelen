package game.NetMessage;

import game.GameApp;
import game.Main;
import game.VectorI;

import java.nio.ByteBuffer;

public class RequestChunk extends Message {
    static byte id = 4;

    int x;
    int y;

    public RequestChunk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public RequestChunk(ByteBuffer message) {
        this.x = message.getInt();
        this.y = message.getInt();
    }

    public static byte getId() {
        return id;
    }

    public byte[] toBytes() {
        System.out.println("Requests chunk "+x+" ; "+y);
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2);
        message.put(id);
        message.putInt(x);
        message.putInt(y);
        return message.array();
    }

    @Override
    public void process() {
        if (!Main.isServer())
            return;

        VectorI indexes = new VectorI(x, y);
        var subworld = Main.getGame().getActiveSubworld();
        var chunk = subworld.getActiveChunk(indexes);
        if (chunk != null) {
            senderConnection.addMessage(new ChunkSync(chunk));
            return;
        }
        subworld.loadChunk(indexes);
        senderConnection.addMessage(new ChunkSync(subworld.getActiveChunk(indexes)));
    }
}
