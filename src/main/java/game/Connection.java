package game;

import game.NetMessage.Hello;
import game.NetMessage.Message;
import game.request.PlayerSpawnRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    private short connectionId;
    private final InetAddress playerAddress;
    private final int playerPort;
    private final BlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
    private DatagramSocket socket;
    private boolean connected = false;

    public Connection(DatagramSocket socket, short connectionId, InetAddress playerAddress, int playerPort) {
        this.connectionId = connectionId;
        this.socket = socket;
        this.playerAddress = playerAddress;
        this.playerPort = playerPort;
    }

    public short getConnectionId() { return connectionId; }
    public void setConnectionId(short id) { connectionId = id; }

    public InetAddress getPlayerAddress() {
        return playerAddress;
    }

    public int getPlayerPort() {
        return playerPort;
    }



    public void startSession() {
        connected = true;
        messagesQueue.add(new Hello(connectionId));

//        Main.getGame().addRequest(new PlayerSpawnRequest(this));
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
            System.out.println("Connection::sender " + e.getMessage());
        }
    }

    public void addMessage(Message message) {
//        System.out.println("Adds message for " + this + " to " + playerAddress);
        messagesQueue.add(message);
    }

    public void sendToClient() throws InterruptedException, IOException {
        Message message = messagesQueue.take();
        byte[] dataToSend = message.toBytes();
        System.out.println("Sends " + dataToSend[0] + " to " + connectionId + " at " + playerAddress.getHostAddress());
        DatagramPacket packetToClient = new DatagramPacket(dataToSend, dataToSend.length,
                playerAddress, playerPort);

        socket.send(packetToClient);
    }

}
