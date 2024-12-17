package game.NetMessage;

import game.Main;
import game.Material;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ContentSync extends Message {

    static byte id = 3;
//    static {Messages.addMessage(new FirstSync(null));}

    private ArrayList<String> materials;


    public ContentSync(ArrayList<String> materials) {
        this.materials = materials;
    }

    public ContentSync(ByteBuffer message) {
        materials = new ArrayList<>();
        byte[] bytes = new byte[message.remaining()];
        message.get(bytes);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                materials.add(new String(bytes, index, i - index, StandardCharsets.UTF_8));
                if (i < bytes.length - 1 && bytes[i+1] == 0)
                    break;
                index = i + 1;
            }
        }
    }

    public static byte getId() {
        return id;
    }

    @Override
    public byte[] toBytes() {
        int size = 0;
        for (String material : materials) {
            size += (material.getBytes(StandardCharsets.UTF_8).length)+1;
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
    public void process(){
        Main.getGame().getActiveWorld().receiveContentFromServer(materials);
    }
}
