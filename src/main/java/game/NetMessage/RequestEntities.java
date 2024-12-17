package game.NetMessage;

import game.Entity;
import game.GameApp;
import game.Main;
import game.request.ChunkRequest;
import game.request.EntitiesRequest;

import java.nio.ByteBuffer;

public class RequestEntities extends Message {
    static byte id = 9;

    public RequestEntities() {}

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
        if (!Main.isServer())
            return;

        var subworld = Main.getGame().getActiveSubworld();
        var entities = subworld.getEntities();

        senderConnection.setInitialized(true);
//        senderConnection.addMessage(entities.getFirst().getSpawnMessage());
        for (Entity entity : entities) {
            System.out.println("Will send entity " + entity.getId());
            senderConnection.addMessage(entity.getSpawnMessage());
        }
    }
}
