package game.NetMessage;

import game.GameApp;
import game.Main;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Hello extends Message {
    static byte id = 0;
//    static {Messages.addMessage(new Hello());}
    static InetAddress address;
    static int port;

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
        //if handshake with server successful
        if(state == GameApp.GameState.Client){
            Main.getClient().setServerActive(true);
            System.out.println("Connected to server");
        }

    }
}