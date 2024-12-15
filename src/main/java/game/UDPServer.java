package game;

import com.sun.source.tree.Tree;
import game.NetMessage.Hello;
import game.NetMessage.Messages;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class UDPServer implements Runnable {
    private int maxPacketSize = 1060;
    private byte[] buffer = new byte[maxPacketSize];
//    private byte[] reserveBuffer;
    private final DatagramSocket socket;
    private Connection currentConnection;
    private final TreeSet<Connection> connections = new TreeSet<>(
            (o1, o2) -> o2.getConnectionId() - o1.getConnectionId());
    private static final Random random = new Random();
    private static final byte CONNECTION_ID_BYTES = Short.BYTES;

    public UDPServer(int maxPacketSize, int port)throws IOException {
        this.socket = new DatagramSocket(port);
        this.maxPacketSize = maxPacketSize;
    }

    public UDPServer(int maxPacketSize) throws IOException {
        this.socket = new DatagramSocket();
        this.maxPacketSize = maxPacketSize;
    }

    public UDPServer() throws IOException {
        this.socket = new DatagramSocket();
    }


    @Override
    public void run() {//main server cycle
        System.out.printf("Server started on local IP %s%n", socket.getLocalAddress().getHostAddress());
//        System.out.printf("Server Name local IP %s%n", socket.getLocalAddress().getHostName());
//        System.out.printf("Server started on IP %s%n", socket.getInetAddress().getHostAddress());
//        System.out.printf("Server Name IP %s%n", socket.getInetAddress().getHostName());
        System.out.printf("Server started on port %d%n", socket.getLocalPort());
        receiver();
    }

    public Connection getCurrentConnection() {
        return currentConnection;
    }


    public void receiveMessage(ByteBuffer bytesFromClient) {
        try {
            Messages.processReceivedBinMessage(bytesFromClient.slice(
                    CONNECTION_ID_BYTES, bytesFromClient.capacity() - CONNECTION_ID_BYTES));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
    ByteBuffer receivedBytes;
    Connection comparingConnection = new Connection(null, (short) 0, null, 0);

    public void receiver(){
        try {
            while (!socket.isClosed()) {
                socket.receive(packetFromClient);
                receivedBytes = ByteBuffer.wrap(packetFromClient.getData());

                if (receivedBytes.get(0) == 0)
                    System.out.println("Received " + + receivedBytes.get(0) + " from "
                            + packetFromClient.getAddress().getHostAddress());
                else
                    System.out.println("Received " + + receivedBytes.get(CONNECTION_ID_BYTES) + " from "
                            + receivedBytes.getShort(0) + " at " + packetFromClient.getAddress().getHostAddress());

                short connectionId;
                if (receivedBytes.get(0) == 0) {
                    currentConnection = new Connection(socket, (short) random.nextInt(),
                                    packetFromClient.getAddress(), packetFromClient.getPort());
                    connections.add(currentConnection);
                    currentConnection.startSession();
                    connectionId = currentConnection.getConnectionId();
                    System.out.println("Client " + connectionId + " Connected");
                } else {
                    connectionId = receivedBytes.getShort(0);
                    comparingConnection.setConnectionId(connectionId);
                    currentConnection = connections.ceiling(comparingConnection);
                    if (currentConnection == null || currentConnection.getConnectionId() != connectionId)
                        continue;
                }
                receiveMessage(receivedBytes);
                currentConnection = null;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void shutdown() {
        System.out.println("Server stopped");
        socket.close();
//        Thread.currentThread().interrupt();
    }
}

