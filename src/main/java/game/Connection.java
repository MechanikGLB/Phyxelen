package game;

import game.NetMessage.Hello;
import game.NetMessage.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    private final InetAddress playerAddress;
    private final int playerPort;
    private final BlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
    private DatagramSocket socket;
    private boolean connected = false;

    public Connection(DatagramSocket socket, InetAddress playerAddress, int playerPort) {
        this.socket = socket;
        this.playerAddress = playerAddress;
        this.playerPort = playerPort;
    }

    public InetAddress getPlayerAddress() {
        return playerAddress;
    }

    public int getPlayerPort() {
        return playerPort;
    }



    public void startSession() {
        connected = true;
        messagesQueue.add(new Hello());
        Thread sender = new Thread(this::sender);
        sender.start();
        System.out.println("Client Handler started");
    }

    public void closeSession(Thread thread) {
        connected = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Connection connection = (Connection) obj;
        return playerPort == connection.playerPort
                && playerAddress.equals(connection.playerAddress);
    }

    protected void sender(){
        try {
            while (!socket.isClosed() && connected)
                sendToClient();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //    public Message takeMessage() throws InterruptedException {
//        return messagesQueue.take();
//    }
    public void addMessage(Message message) {
        messagesQueue.add(message);
    }

    public void sendToClient() throws InterruptedException, IOException {
        Message message = messagesQueue.take();
        byte[] dataToSend = message.toBytes();
        System.out.println("Sends " + dataToSend[0] + " to " + playerAddress.getHostAddress());
        DatagramPacket packetToClient = new DatagramPacket(dataToSend, dataToSend.length,
                playerAddress, playerPort);

        socket.send(packetToClient);
    }

}
