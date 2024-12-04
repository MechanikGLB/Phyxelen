package game.NetMessage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Request extends Message {
    static byte id = Messages.getNextMessageIndex();
    byte requestedID; //ID of the requested message
    int requestData = 0; // information with request to server(optional)

    public Request(byte requestedID) {
        this.requestedID = requestedID;
    }

    public Request(byte requestedID,int requestData) {
        this.requestedID = requestedID;
        this.requestData = requestData;
    }

    public byte[] buildMessage() {
            ByteBuffer message = ByteBuffer.allocate(2+Integer.BYTES);
            message.put(id);
            message.put(requestedID);
            message.putInt(requestData);
            return message.array();
        }
}
