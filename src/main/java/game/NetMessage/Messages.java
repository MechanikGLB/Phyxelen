package game.NetMessage;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class Messages {
    static byte messageCounter = 0;
    static byte getNextMessageIndex() {
        return messageCounter++;
    }
    static List<Message> messages = Arrays.asList(
            new Hello(),
            new Quit(),
            new RequestContent(),
            new ContentSync(null),
            new RequestChunk(0, 0),
            new ChunkSync(),
            new RequestPlayerSpawn(),
            new PlayerSpawn(0,0, 0,(short) 0),
            new ProjectileSpawn(0),
            new RequestEntities(),
            new PlayerSync(null)
    );

//    public static void addMessage(Message message) {
//        messages.add(message);
//    }
    public static Message getMessage(int id) { return messages.get(id); }

    public static void processReceivedBinMessage(ByteBuffer message) {
        System.out.println("Process message " + message.get(0));
//        if (message.capacity() == 1)
//            messages.get(message.get(0)).processMessage(message);
        messages.get(message.get(0)).processReceivedBinMessage(message.slice(1, message.capacity()-1));
    }

    public static Message getType(ByteBuffer message) {
        return messages.get(message.get(0));
    }

}
