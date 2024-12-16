package game;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import game.NetMessage.*;

//FOR TESTS ONLY
//This Class will be renamed or deleted in future!

public class UDPClient implements Runnable {
    public short connectionId = 0;

    private Client gameClient;
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();//consumer-producer
    private final int maxPacketSize = 1060;
    private final byte[] buffer = new byte[maxPacketSize];
    private final int[] timeouts = {11, 29, 73, 277, 997};
    private boolean serverActive = false;


    public UDPClient(String ip, int port) throws IOException {
        this.address = InetAddress.getByName(ip);//Server addr
        this.port = port;//Server port
        this.socket = new DatagramSocket();
        gameClient = (Client) Main.getGame();
    }

//    public UDPClient(String ip, int port, int maxPacketSize) throws IOException {
//        this.maxPacketSize = maxPacketSize;
//        this.address = InetAddress.getByName(ip);//Server addr
//        this.port = port;//Server port
//        this.socket = new DatagramSocket();
//    }

    @Override
    public void run(){
        try {

            System.out.println("UDP Client started");
            queue.add(new Hello()); // Execute handshake
            sendToServer();
            responseReceive();
            if (serverActive) {
                Thread ReceiveHandler = new Thread(this::receiver);
                Thread SendHandler = new Thread(this::sender);
                ReceiveHandler.start();
                SendHandler.start();
            }
            else {
                System.out.println("Server timed out");
            }

        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendToServer() throws InterruptedException {
        Message message = queue.take();
        byte[] messageByteArray = message.toBytes();
        ByteBuffer dataToSend = ByteBuffer.allocate(messageByteArray.length + Short.BYTES);
        dataToSend.putShort(connectionId);
        dataToSend.put(messageByteArray);

        System.out.println("Sends " + dataToSend.get(dataToSend.capacity() < 2 ? 0 : 2));
        DatagramPacket packetToServer = new DatagramPacket(dataToSend.array(), dataToSend.capacity(),
                address, port);

        for (int timeout : timeouts) {
            try {
                socket.setSoTimeout(timeout);
                socket.send(packetToServer);

                responseReceive();
                break;
            } catch (IOException e) {
                System.out.println("Server timeout");
            }
        }
    }

    public void addMessage(Message message) {
        System.out.println("Add message "+message);
        queue.add(message);
    }

    public void receiver(){
        try {
            while (!socket.isClosed())
                responseReceive();
        } catch (Exception e) {
            throw e;
        }
    }

    public void sender(){
        try {
            while (!socket.isClosed())
                sendToServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
//            System.out.println(e.getMessage());
        }
    }

    DatagramPacket packetFromServer = new DatagramPacket(buffer, buffer.length);

    public void responseReceive() {
        try {
            socket.receive(packetFromServer);
            System.out.println("Received from Server: " + packetFromServer.getData()[0]);

            gameClient.addMessage(Message.make(ByteBuffer.wrap(packetFromServer.getData())));
        } catch (IOException e) {
//            System.out.println("UDPClient::responseReceive " + e.getMessage());
        }
    }

    public void shutdown() {
        queue.add(new Quit());
        socket.close();
        System.out.println("Client stopped");
        Thread.currentThread().interrupt();
    }

    public void setServerActive(boolean serverActive) {
        this.serverActive = serverActive;
    }
//    public boolean isServerActive() {
//        return serverActive;
//    }

}
