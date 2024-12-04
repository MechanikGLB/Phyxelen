package game.NetMessage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class EntityPosition {
    static byte id = Messages.getNextMessageIndex();
    short entityId;
    float x;
    float y;

    public EntityPosition(short id, float x, float y) {
        entityId = id;
        this.x = x;
        this.y = y;
    }

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1+Integer.BYTES+Float.BYTES*2);
        message.put(id);
        message.putShort(entityId);
        message.putFloat(x);
        message.putFloat(y);
        return message.array();
    }
}
