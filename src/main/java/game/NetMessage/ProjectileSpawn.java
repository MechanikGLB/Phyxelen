package game.NetMessage;

import game.GameObject;
import game.Main;
import game.Player;
import game.Subworld;

import java.nio.ByteBuffer;

public class ProjectileSpawn extends Message {
    static byte id = 6;
    float x;
    float y;
    int entityId;

    public ProjectileSpawn() {

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

    }
}
