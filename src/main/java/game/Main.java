package game;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    private static GameApp game;
    private static UDPServer server;
    private static UDPClient client;
    private static Thread netThread;

    public static GameApp getGame() { return game; }
    public static UDPServer getServer() { return server; }
    public static UDPClient getClient() { return client; }

    public static void main(String[] args) {
        game = new Client();
        System.out.println(Arrays.toString(args));
        if (args.length == 1) {
            if (args[0].equals("-s")) {

                game.gameState = GameApp.GameState.Server;
                try {
                    server = new UDPServer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                netThread = new Thread(() -> server.run());
                netThread.run();
                game.run();
                server.shutdown();
            }
        } else if (args.length == 2) {
            if (args[0].equals("-s")) {
                game.gameState = GameApp.GameState.Server;
                try {
                    server = new UDPServer(1024, Integer.parseInt(args[1]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                netThread = new Thread(() -> server.run());
                netThread.start();
                game.run();
                server.shutdown();
            }
        } else if (args.length == 3) {
            if (args[0].equals("-c")) {
                game.gameState = GameApp.GameState.Client;
                try {
                    client = new UDPClient(args[1], Integer.parseInt(args[2]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                client.run();
                netThread = new Thread(() -> client.run());
                netThread.start();
                game.run();
                client.shutdown();
            }
        } else {
            game.gameState = GameApp.GameState.Local;
            game.run();
        }
    }
}

