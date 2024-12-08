package game;

import game.NetMessage.Message;
import game.NetMessage.Messages;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class UDPServer implements Runnable {


    private int maxPacketSize = 1024;
    private byte[] buffer = new byte[maxPacketSize];
//    private byte[] reserveBuffer;
    private final DatagramSocket socket;
    private InetAddress sessionAddress;
    private int sessionPort;
    private ArrayList<ConnectedUser> usersOnline = new ArrayList<ConnectedUser>();


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
        System.out.printf("Server started on IP %s%n", socket.getLocalAddress().getHostAddress());
        System.out.printf("Server started on port %d%n", socket.getLocalPort());
        while (true) {
            if (socket.isClosed())
                return;
            try {

                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                //TODO:SessionManager
                receiveMessage(packetFromClient);
                //TODO: Server logic
                System.out.println("Server received: " + new String(packetFromClient.getData()));
            }
            catch (Exception e) {
                if (socket.isClosed())
                    return;
                throw new RuntimeException(e);
            }
        }
    }

    public void NetListener() throws IOException {

    }

    public void receiveMessage(DatagramPacket packetFromClient) {
        try {
            socket.receive(packetFromClient);//blocks thread until received
            int packetLength = packetFromClient.getLength();
            System.out.println(new String(packetFromClient.getData()));
            //TODO: Rework logic
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clientHandler(){
        try {
            while (!socket.isClosed()) {
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetFromClient);
                if ((Messages.getType(ByteBuffer.wrap(packetFromClient.getData())).getClass().getName()) == "Hello"){
                    Thread clientSession = new Thread(this::SessionHandler);
                    //TODO: Check existence of player
                }
            }
        }
        catch (Exception e) {}
    }

    public void SessionHandler(){
        //TODO:SessionHandler
    }

    public void sendToClient(Message message, InetAddress clientAddr, int clientPort) throws IOException {
        byte[] dataToSend = message.buildMessage();
        DatagramPacket packetToClient = new DatagramPacket(dataToSend, dataToSend.length,
                clientAddr, clientPort);

                socket.send(packetToClient);
        }
//        byte[] dataBuffer = message.getBytes(); //NOW WE ARE TRUST THAT MESSAGE NOT BIGGER THAN PACKET
//        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length, clientAddr, clientPort);
//        socket.send(packet); //large packets separates automatically
//        System.out.println("Server sent message: " + message);


    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public void shutdown() {
        System.out.println("Server stopped");
        socket.close();
//        Thread.currentThread().interrupt();
    }
}

class ConnectedUser {
    private InetAddress playerAddress;
    private int playerPort;
    private BlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();

    public ConnectedUser(InetAddress playerAddress, int playerPort) {
        this.playerAddress = playerAddress;
        this.playerPort = playerPort;
    }

    public InetAddress getPlayerAddress() {
        return playerAddress;
    }

    public int getPlayerPort() {
        return playerPort;
    }

    public Message takeMessage() throws InterruptedException {
        return messagesQueue.take();
    }
    public void addMessage(Message message) {
        messagesQueue.add(message);
    }

}

