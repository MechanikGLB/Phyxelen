import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.io.*;
import java.util.List;
import java.util.Map;

import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class World {
    String name; /// World displayed name
    String path; /// World folder name

    HashMap<String, Subworld> subworlds;
    String defaultSubworldId;
    private String[] modules;
    // Array for mapping int to PixelDefinition
    Material[] pixelIds;

    public World() {
        subworlds = new HashMap<>();
//        for (int i = 0; i < subworlds.length; i++)
//            subworlds[i] = new Subworld(this);
    }


    public void tick(float dt) {
        for (Subworld subworld : subworlds.values()) {
            subworld.update(dt);
        }
    }


    public static World createWorld(String worldName, String gameName) {
        var gameConfig = Main.getGame().content.getGameConfig(gameName);

        List<java.lang.Character> forbiddenChars = Arrays.asList('/','\\',':','*','?','"','<','>','|');
        StringBuilder path = new StringBuilder();
        for (char ch : worldName.toCharArray()) {
            if (forbiddenChars.contains(ch))
                path.append('_');
            else
                path.append(ch);
        }

        World world = new World();
        world.defaultSubworldId = (String) gameConfig.get("default_subworld");
        if (world.defaultSubworldId == null)
            world.defaultSubworldId = "default";
        world.path = path.toString();
        world.name = worldName;

        File config = new File("worlds" + File.separator + world.path + File.separator + "world.yaml");

        Dump yamlDump = new Dump(
                DumpSettings.builder().build()
        );

        try {
            Files.createDirectories(Path.of("worlds", world.path, "subworlds"));
//            Files.createDirectory(Path.of("worlds", world.path));
            config.createNewFile();
            PrintWriter writer = new PrintWriter(config);
            writer.println(yamlDump.dumpToString(Map.of("default_subworld", world.defaultSubworldId)));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        world.startup();
        return world;
    }


    public static World loadWorldByPath(String path) {
        File config = new File("worlds" + File.separator + path + File.separator + "world.yaml");
//        if (!config.exists() || config.isDirectory())
        World world = new World();

        Load yamlLoad = new Load(LoadSettings.builder().build());
        try {
            var configTable = (HashMap<String, Object>) yamlLoad.loadFromReader(new FileReader(config));
            world.defaultSubworldId = (String) configTable.getOrDefault("default_subworld", "default");

            List<String> moduleList = (List<String>) configTable.get("modules");
            String[] moduleArray = new String[moduleList.size()];
            moduleList.toArray(moduleArray);
            world.modules = moduleArray;

            world.startup();
            return world;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No world \"" + path + "\"");
        } catch (ClassCastException e ) {
            throw new RuntimeException("Config file for " + path + " has wrong structure: " + e.toString());
        }
    }


    public Subworld loadOrCreateSubworld(String subworldId) {
        Subworld subworld = new Subworld(this, subworldId);
        subworlds.put(subworldId, subworld);
        return subworld;
    };


    private void startup() {
        loadContent();
    }


    private void loadContent() {
        assert modules.length > 0;
        Content content = Main.getGame().content;
        content.loadModules(modules);
        //temp
        pixelIds = new Material[content.pixelDefinitions.size() + 1];
        pixelIds[0] = new MaterialAir();
        pixelIds[0].colors = new ColorWithAplha[1];
        pixelIds[0].colors[0] = new ColorWithAplha(0.2f, 0.1f, 0.0f, 1f);
        pixelIds[0].density = 0.01f;
        int i = 1;
        for (var definition : content.pixelDefinitions.values()) {
            pixelIds[i] = definition;
            i++;
        }
    }
}
