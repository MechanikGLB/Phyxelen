import java.io.File;

public abstract class GameApp {
    public enum GameState {
        MainMenu,
        Local, // Single player
        Server,
        Client
    }
    protected GameState gameState;
    protected Content content;
    protected World activeWorld;
    protected Subworld activeSubworld;
    /// Counter is used for making some computations more rare
    protected short counter = 0;


    public GameState getGameState() {return gameState;}


    public void run() {

        // temp
        File testWorldDir = new File("test");
        if (!testWorldDir.exists())
            World.createWorld("test");
        activeWorld = World.loadWorldByPath("test");

        activeSubworld = activeWorld.loadOrCreateSubworld(activeWorld.defaultSubworldId);
    };

    protected abstract void loop();

    protected void tick(float dt) {
//        activeWorld.tick(dt);
    }
}
