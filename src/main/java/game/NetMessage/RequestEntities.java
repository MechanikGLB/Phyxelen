package game.NetMessage;

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
    public void processReceivedBinMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        if (state == GameApp.GameState.Server)
            GameApp.getRequests().add( new EntitiesRequest(Main.getServer().getCurrentConnection()) );
    }
}
