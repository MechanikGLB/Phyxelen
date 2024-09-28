import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public abstract class GameApp {
    public enum GameState {
        MainMenu,
        Local, // Single player
        Server,
        Client
    }
    protected GameState gameState;
    protected Content content = new Content();
    protected World activeWorld;
    protected Subworld activeSubworld;
    /// Counter is used for making some computations more rare
    protected short counter = 0;


    public GameState getGameState() {return gameState;}


    public void run() {
        checkDirectoryStructure();
        // temp
        File testWorldDir = new File("worlds" + File.separator + "test");
        if (!testWorldDir.exists())
            World.createWorld("test", "test");
        activeWorld = World.loadWorldByPath("test");

        activeSubworld = activeWorld.loadOrCreateSubworld(activeWorld.defaultSubworldId);
    };

    protected abstract void loop();

    protected void tick(float dt) {
        activeWorld.tick(dt);
    }

    private void chechFolder(Path path) {
        Files.exists(path);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkDirectoryStructure() {
        for (Path path : Arrays.asList(
                Path.of("worlds"),
                Path.of("games"),
                Path.of("mods")
        )) {
            chechFolder(path);
        }
    }
}
