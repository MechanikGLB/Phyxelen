package game.NetMessage;

import game.Client;
import game.Main;

import javax.lang.model.type.NullType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FirstSync extends Message {

    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new FirstSync(null));}

    private ArrayList<String> materials;


    public FirstSync(ArrayList<String> materials) {
        this.materials = materials;
    }

    public static byte getId() {
        return id;
    }

    @Override
    public byte[] buildMessage() {
        int size = 0;
        for (String material : materials) {
            size += (material.getBytes().length/2)+1;
        }
        ByteBuffer message = ByteBuffer.allocate(1+size);
        message.put(id);
        for (String material : materials) {
            for (char c : material.toCharArray()) {
                message.put((byte) c);
            }
            message.put((byte) 0);
        }
        return message.array();
    }

    @Override
    public void processMessage(ByteBuffer message){

        ArrayList<String> result = new ArrayList<>();
        byte[] bytes = new byte[message.remaining()];
        message.get(bytes);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                result.add(new String(bytes, index, i - index));
                index = i + 1;
            }
        }
        Main.getGame().getActiveWorld().requireContentFromServer(result);
    }
}
