package game;

import game.NetMessage.Hello;
import game.NetMessage.Messages;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class UDPServer implements Runnable {


    private int maxPacketSize = 1060;
    private byte[] buffer = new byte[maxPacketSize];
//    private byte[] reserveBuffer;
    private final DatagramSocket socket;
    private Connection currentConnection;
    private ArrayList<Connection> playersList = new ArrayList<Connection>();


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


    public void receiveMessage(DatagramPacket packetFromClient) {
        try {
            Messages.processReceivedBinMessage(ByteBuffer.wrap(packetFromClient.getData()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void receiver(){
        try {
            while (!socket.isClosed()) {
                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetFromClient);
                System.out.println("Received " + + packetFromClient.getData()[0] + " from "
                        + packetFromClient.getAddress().getHostAddress());
                Connection userToProcess = new Connection(socket, packetFromClient.getAddress(), packetFromClient.getPort());
                if(playersList.contains(userToProcess)) {
                    currentConnection = userToProcess; //for messages work
                }
                if (packetFromClient.getData()[0] == Hello.getId()) {

                    if(!playersList.contains(userToProcess))
                    {
                        System.out.println("Client Connected");
                        playersList.add(userToProcess);
                        playersList.getLast().startSession();
                    }

                }
                receiveMessage(packetFromClient);
                currentConnection = null;
                userToProcess = null;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

