package game.NetMessage;

import java.nio.ByteBuffer;

public class EntityPosition extends Message {
    static byte id = Messages.getNextMessageIndex();
//    static {Messages.addMessage(new EntityPosition((short) 0,0f,0f));}

    short entityId;
    float x;
    float y;

    public EntityPosition(short id, float x, float y) {
        entityId = id;
        this.x = x;
        this.y = y;
    }

    public static byte getId() {
        return id;
    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1+Integer.BYTES+Float.BYTES*2);
        message.put(id);
        message.putShort(entityId);
        message.putFloat(x);
        message.putFloat(y);
        return message.array();
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {

    }
}
