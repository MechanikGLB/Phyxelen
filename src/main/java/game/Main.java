package game;

public class Main {

    private static GameApp game;

    public static GameApp getGame() {
        return game;
    }

    public static void main(String[] args) {
        game = new Client();
        game.run();
    }
}

