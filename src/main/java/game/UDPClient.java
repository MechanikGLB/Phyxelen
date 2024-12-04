package game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private int maxPacketSize = 1024;
    private final byte[] buffer = new byte[maxPacketSize];
    private final int[] timeouts = {11, 29, 73, 277, 997};

    public UDPClient(String ip, int port) throws IOException {
        this.address = InetAddress.getByName(ip);//Server addr
        this.port = port;//Server port
        this.socket = new DatagramSocket();
    }

    public UDPClient(String ip, int port, int maxPacketSize) throws IOException {
        this.maxPacketSize = maxPacketSize;
        this.address = InetAddress.getByName(ip);//Server addr
        this.port = port;//Server port
        this.socket = new DatagramSocket();
    }

    @Override
    public void run(){
        try {
            queue.add(new Hello());
            while (!socket.isClosed()) {
                Message message = queue.take();
                sendToServer(message.buildMessage());
                //TODO:communication logic
            }
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }
    //TODO:Upgrade to send(type,data) with metadata
    public void sendToServer(byte[] dataToSend) {
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

    public void responseReceive() throws IOException {
        DatagramPacket packetFromServer = new DatagramPacket(buffer, buffer.length);
        socket.receive(packetFromServer);
        System.out.println(new String(packetFromServer.getData()));//replace with new logic
    }

    public void shutdown() {
        System.out.println("Client stopped");
        queue.add(new Quit());
        socket.close();
//        Thread.currentThread().interrupt();
    }
}
