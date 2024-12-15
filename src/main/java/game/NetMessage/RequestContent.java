package game.NetMessage;

import game.GameApp;
import game.Main;

import java.nio.ByteBuffer;

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
    public void processReceivedBinMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        if (state == GameApp.GameState.Server)
            Main.getServer().getCurrentConnection().addMessage(ContentSync.makeMessage());
    }
}
