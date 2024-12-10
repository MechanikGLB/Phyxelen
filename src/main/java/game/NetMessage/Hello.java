package game.NetMessage;

import game.GameApp;
import game.Main;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;

public class Hello extends Message {
    static byte id = 0;
//    static {Messages.addMessage(new Hello());}
    static Random random = new Random();
    short connectionId;


    public static byte getId() {
        return id;
    }

    public Hello() {};

    public Hello(short connectionId) {
        this.connectionId = connectionId;
    }

    public byte[] toBytes() {
        if (Main.getGame().getGameState() == GameApp.GameState.Client) {
            ByteBuffer message = ByteBuffer.allocate(1);
            message.put(id);
            return message.array();
        } else {
            ByteBuffer message = ByteBuffer.allocate(1 + Short.BYTES);
            message.put(id);
            message.putShort(connectionId);
            return message.array();
        }
    }

    @Override
    public void processReceivedBinMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        //if handshake with server successful
        if (state == GameApp.GameState.Client) {
            var client = Main.getClient();
            client.setServerActive(true);
            client.connectionId = message.getShort();
            System.out.println("Connected to server as " + client.connectionId);
        }
    }
}