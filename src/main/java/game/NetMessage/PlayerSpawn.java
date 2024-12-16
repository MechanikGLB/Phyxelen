package game.NetMessage;

import game.*;
import game.request.ReceivedPlayerRequest;

import java.nio.ByteBuffer;

public class PlayerSpawn extends Message {
    static byte id = 7;
    int x;
    int y;
    int entityId;
    boolean isLocal = false;
    short seed;

    public PlayerSpawn(int x, int y, int id,short seed) {
        this.x = x;
        this.y = y;
        this.entityId = id;
        this.seed = seed;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 3 + 1+ Short.BYTES);
        message.put(id);
        message.putInt(entityId);
        message.putInt(x);
        message.putInt(y);
        message.put((byte) (isLocal ? 1 : 0));
        message.putShort(seed);
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        Subworld subworld = Main.getGame().getActiveSubworld();

        int entityId = message.getInt();
        System.out.println("Received player "+entityId);
        int x = message.getInt();
        int y = message.getInt();
        boolean isLocal = message.get() == 1;
        short seed = message.getShort();

        Main.getGame().addRequest(new ReceivedPlayerRequest(null, x, y, entityId, seed));
    }
}
