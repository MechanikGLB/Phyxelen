package game.NetMessage;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Messages {
    static byte messageCounter = 0;
    static byte getNextMessageIndex() {
        return messageCounter++;
    }
    static ArrayList<Message> messages = new ArrayList<>();

    public static void addMessages(Message message) {
        messages.add(message);
    }


    public static void process(ByteBuffer message) {
        messages.get(message.get(0)).processMessage(message.slice(1, message.capacity()));
    }

    public static Message getType(ByteBuffer message) {
        return messages.get(message.get(0));
    }

}
