package game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import game.NetMessage.*;

//FOR TESTS ONLY
//This Class will be renamed or deleted in future!

public class UDPClient implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();//consumer-producer
    private final int maxPacketSize = 1024;
    private final byte[] buffer = new byte[maxPacketSize];
    private final int[] timeouts = {11, 29, 73, 277, 997};
    private boolean serverActive = false;

    public UDPClient(String ip, int port) throws IOException {
        this.address = InetAddress.getByName(ip);//Server addr
        this.port = port;//Server port
        this.socket = new DatagramSocket();
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
            queue.add(new Hello());//Execute handshake
            sendToServer();
            responseReceive();

            if(serverActive) {
                Thread ReceiveHandler = new Thread(this::receiver);
                Thread SendHandler = new Thread(this::sender);
                ReceiveHandler.start();
                SendHandler.start();
                queue.add(new Request(FirstSync.getId()));
            }
            else {
                System.out.println("Server timed out");
            }

        }
        catch (InterruptedException e) {
            e.getStackTrace();
        }
    }

    protected void sendToServer() throws InterruptedException {
        Message message = queue.take();
        byte[] dataToSend = message.buildMessage();
        System.out.println("_____________________________________________");
        System.out.println("Sent to Server: " + dataToSend[0]);
        DatagramPacket packetToServer = new DatagramPacket(dataToSend, dataToSend.length,
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
        queue.add(message);
    }

    public void receiver(){
        try {
            while (!socket.isClosed())
                responseReceive();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sender(){
        try {
            while (!socket.isClosed())
                sendToServer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void responseReceive() {
        try {
            DatagramPacket packetFromServer = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetFromServer);
            System.out.println("Received from Server: " + packetFromServer.getData()[0]);
            System.out.println("__________________________________");
            Messages.process(ByteBuffer.wrap(packetFromServer.getData()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
