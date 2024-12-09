package game;

import game.NetMessage.Hello;
import game.NetMessage.Message;
import game.NetMessage.Messages;

import javax.sound.midi.Receiver;
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
//    private InetAddress sessionAddress;
//    private int sessionPort;
    private ArrayList<ConnectedUser> playersList = new ArrayList<ConnectedUser>();


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

    public void receiveMessage(DatagramPacket packetFromClient) {
        try {
            Messages.process(ByteBuffer.wrap(packetFromClient.getData()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void receiver(){
        try {
            while (!socket.isClosed()) {
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetFromClient);
                System.out.println(packetFromClient.getAddress().getHostAddress());
                System.out.println(packetFromClient.getData()[0]);
                ConnectedUser userToProcess = new ConnectedUser(socket, packetFromClient.getAddress(), packetFromClient.getPort());
                if (packetFromClient.getData()[0] == Hello.getId()){
                    System.out.println("Client Connected");
                    if(!playersList.contains(userToProcess))
                    {
                        playersList.add(userToProcess);
                        playersList.getLast().startSession();
                    }
                }
                receiveMessage(packetFromClient);
            }
        }
        catch (Exception e) {}
    }

//    public void sendToClient(Message message, InetAddress clientAddr, int clientPort) throws IOException {
//        byte[] dataToSend = message.buildMessage();
//        DatagramPacket packetToClient = new DatagramPacket(dataToSend, dataToSend.length,
//                clientAddr, clientPort);
//
//                socket.send(packetToClient);
//        }
//        byte[] dataBuffer = message.getBytes(); //NOW WE ARE TRUST THAT MESSAGE NOT BIGGER THAN PACKET
//        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length, clientAddr, clientPort);
//        socket.send(packet); //large packets separates automatically
//        System.out.println("Server sent message: " + message);


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

class ConnectedUser {
    private final InetAddress playerAddress;
    private final int playerPort;
    private final BlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
    private DatagramSocket socket;
    private boolean connected = false;

    public ConnectedUser(DatagramSocket socket, InetAddress playerAddress, int playerPort) {
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
        byte[] dataToSend = message.buildMessage();
        DatagramPacket packetToClient = new DatagramPacket(dataToSend, dataToSend.length,
                playerAddress, playerPort);

        socket.send(packetToClient);
    }

}

