package game.NetMessage;

import game.GameApp;
import game.Main;

import java.nio.ByteBuffer;

public class RequestChunk extends Message {
    static byte id = 4;
//    static {Messages.addMessage(new RequestChunk(0, 0));}

    int x;
    int y;

    public RequestChunk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static byte getId() {
        return id;
    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2);
        message.put(id);
        message.putInt(x);
        message.putInt(y);
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        if (state == GameApp.GameState.Server)
            Main.getServer().getCurrentConnection().addMessage(
                    new ChunkSync(message.getInt(0), message.get(1)));

    }
}
