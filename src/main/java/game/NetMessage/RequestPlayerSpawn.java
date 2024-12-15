package game.NetMessage;

import game.GameApp;
import game.Main;
import game.request.ChunkRequest;
import game.request.PlayerSpawnRequest;

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
    public void processReceivedBinMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        if (state == GameApp.GameState.Server)
            GameApp.getRequests().add(
                    new PlayerSpawnRequest(Main.getServer().getCurrentConnection()));
    }
}
