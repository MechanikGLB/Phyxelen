package game.NetMessage;

import game.Entity;
import game.Main;
import game.Player;
import game.Subworld;

import java.nio.ByteBuffer;

public class PlayerSpawn extends Message {
    static byte id = 6;
    float x;
    float y;
    int entityId;

    public PlayerSpawn(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.entityId = id;
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Float.BYTES * 2 + Integer.BYTES );
        message.put(id);
        message.putInt(id);
        message.putFloat(x);
        message.putFloat(y);
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        Subworld subworld = Main.getGame().getActiveSubworld();
        float x = message.getFloat();
        float y = message.getFloat();
        int entityId = message.getInt();
        Player playerToSpawn = new Player(x, y, subworld);

        Main.getGame().getActiveSubworld().spawnPlayer(playerToSpawn);
    }
}
