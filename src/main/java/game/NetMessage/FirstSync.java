package game.NetMessage;

import game.Main;
import game.Material;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FirstSync extends RequestableMessage {

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

    static public Message makeMessage() {
        ArrayList<String> materials = new ArrayList<>();

        var materialsByID = Main.getGame().getActiveWorld().getMaterialsById();
        for (Material material : materialsByID) {
            materials.add(material.getName());
        }
        return new FirstSync(materials);
    }

    @Override
    public Message makeMessageByRequest() {
        return makeMessage();
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
        Main.getGame().getActiveWorld().receiveContentFromServer(result);
    }
}
