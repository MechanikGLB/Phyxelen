package game.NetMessage;

import java.nio.ByteBuffer;

public class Quit extends Message {
    static byte id = 1;
//    static {Messages.addMessage(new Quit());}

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }

    @Override
    public void process() {

    }
}
