package game.NetMessage;

import game.Client;
import game.GameApp;
import game.Main;
import game.UDPClient;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Hello extends Message {
    static byte id = Messages.getNextMessageIndex();
    static {Messages.addMessages(new FirstSync(null));}
    static InetAddress address;
    static int port;

    public static byte getId() {
        return id;
    }

    public byte[] buildMessage() {
        ByteBuffer message = ByteBuffer.allocate(1);
        message.put(id);
        return message.array();
    }

    @Override
    public void processMessage(ByteBuffer message) {
        GameApp.GameState state = Main.getGame().getGameState();
        //if handshake with server successful
        if(state == GameApp.GameState.Client){
            Main.getClient().setServerActive(true);
        }

    }
}