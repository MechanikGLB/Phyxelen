package game.NetMessage;

import game.Main;
import game.Player;

import java.nio.ByteBuffer;

public class RequestPlayerSpawn extends Message {
    static byte id = 6;

    public RequestPlayerSpawn() {}

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
        Player player = new Player(0,0,subworld, senderConnection);
        subworld.spawnPlayer(player);
        senderConnection.addMessage(player.getSpawnMessage());
    }
}
