package game;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


public class UDPServer implements Runnable {

    private int maxPacketSize = 1024;
    private byte[] buffer = new byte[maxPacketSize];
    private byte[] reserveBuffer;
    private final DatagramSocket socket;


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
        System.out.printf("Server started on IP %s%n", socket.getInetAddress().getHostAddress());
        System.out.printf("Server started on port %d%n", socket.getLocalPort());
        while (true) {
            try {

                DatagramPacket packetFromClient = new DatagramPacket(buffer, buffer.length);
                receiveMessage(packetFromClient);
                //TODO: Server logic
                System.out.println("Server received: " + new String(packetFromClient.getData()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void receiveMessage(DatagramPacket packetFromClient) {
        try {
            socket.receive(packetFromClient);//blocks thread until received
            int packetLength = packetFromClient.getLength();
            sendToClient("Packet got",
                    packetFromClient.getAddress(), packetFromClient.getPort());//Send response to client
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //TODO MORE FUNCTIONS
    public void sendToClient(String message, InetAddress clientAddr, int clientPort) throws IOException {
        byte[] dataBuffer = message.getBytes(); //NOW WE ARE TRUST THAT MESSAGE NOT BIGGER THAN PACKET
        DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length, clientAddr, clientPort);
        socket.send(packet); //large packets separates automatically
        System.out.println("Server sent message: " + message);
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public void setMaxPacketSize(int MaxPacketSize) {
        this.maxPacketSize = MaxPacketSize;
        buffer = new byte[maxPacketSize];
    }



    public void shutdown() {
        socket.close();
        Thread.currentThread().interrupt();
        System.out.println("Server stopped");
    }
}

