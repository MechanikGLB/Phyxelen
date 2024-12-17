package game.NetMessage;

import game.GameApp;
import game.Main;
import game.Player;

import java.nio.ByteBuffer;
import java.util.Random;

public class Initialized extends Message {
    static byte id = 12 ;
    static Random random = new Random();
    short connectionId;


    public static byte getId() {
        return id;
    }

    public Initialized() {};

//    public Initialized(short connectionId) {
//        this.connectionId = connectionId;
//    }

    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }

    @Override
    public void process() {
        GameApp.GameState state = Main.getGame().getGameState();
        //if handshake with server successful
        senderConnection.setInitialized(true);
        var players = Main.getGame().getActiveSubworld().getPlayers();

        // Send character to all connected clients except just connected one
        for (Player player : players) {
            if (player.getConnection() != senderConnection)
                senderConnection.addMessage(new PlayerSpawn(
                        (int) player.getX(),
                        (int) player.getY(),
                        player.getId(),
                        player.getSeed()
            ));
        }
    }
}