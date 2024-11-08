package game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//FOR TESTS ONLY
//This Class will be renamed or deleted in future!

public class UDPClient implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private final int maxPacketSize = 512;
    private final byte[] buffer = new byte[maxPacketSize];
    private final int[] timeouts = {11, 29, 73, 277, 997};

    public UDPClient(String ip, int port) throws IOException {
        this.address = InetAddress.getByName(ip);
        this.port = port;
        this.socket = new DatagramSocket();
    }

    @Override
    public void run(){
        try {
            while (true) {
                byte[] message = queue.take();
                DatagramPacket packetToServer = new DatagramPacket(message, message.length,
                        address, port);

                for (int timeout : timeouts) {
                    try {
                        socket.setSoTimeout(timeout);
                        socket.send(packetToServer);

                        DatagramPacket packetFromServer = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packetFromServer);
                        System.out.println(new String(packetFromServer.getData()));
                        break;
                    } catch (IOException e) {
                        System.out.println("Server timeout");
                    }
                }
            }
        }

        catch (Exception e) {
            e.getStackTrace();
        }
    }
}
