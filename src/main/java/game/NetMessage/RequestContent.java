package game.NetMessage;

import game.GameApp;
import game.Main;
import game.Material;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RequestContent extends Message {
    static byte id = 2;

    public RequestContent() {}

    public static byte getId() {
        return id;
    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }

    @Override
    public void process() {
        if (Main.getGame().getGameState() != GameApp.GameState.Server)
            return;

        ArrayList<String> materials = new ArrayList<>();

        var materialsByID = Main.getGame().getActiveWorld().getMaterialsById();
        for (Material material : materialsByID) {
            materials.add(material.getName());
        }
        senderConnection.addMessage(new ContentSync(materials));
    }
}
