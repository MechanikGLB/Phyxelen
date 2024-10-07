import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;


enum ModuleType {
    Mod,
    Game
}


public class Content {
    HashMap<String, Material> pixelDefinitions = new HashMap<>();
    private final Load yaml = new Load(LoadSettings.builder().build());


    HashMap<String, Object> getGameConfig(String gameName) {
        try {
            Object readed = yaml.loadFromReader(
                    new FileReader("games" + File.separator + gameName + File.separator + "config.yaml"));
            try {
                if (readed instanceof HashMap)
                    return (HashMap<String, Object>) readed;
                return null;
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    void loadModules(String[] moduleNames) {
        for (String moduleName : moduleNames) {
            FileReader reader;
            if (Files.exists(Path.of("games", moduleName, "config.yaml"))) {
                try {
                    reader = new FileReader("games" + File.separator + moduleName + File.separator + "config.yaml");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (Files.exists(Path.of("mods", moduleName, "config.yaml"))) {
                try {
                    reader = new FileReader("mods" + File.separator + moduleName + File.separator + "config.yaml");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                continue;
            }
            Object readed = yaml.loadFromReader(reader);
            try {
                var table = (HashMap<String, Object>) readed;
                Object materials = table.get("materials");
                if (materials != null) {
                    var materialTable = (HashMap<String, HashMap<String, Object>>) materials;
                    for (var material : materialTable.entrySet()) {
                        Material materialDefinition = parseMaterialType(material);
                        if (materialDefinition == null)
                            continue;
                        parseMaterialColors(material, materialDefinition);
                        parseMaterialDensity(material, materialDefinition);
                        pixelDefinitions.put(material.getKey(), materialDefinition);
                    }
                }
            } catch (ClassCastException e) {
                throw new RuntimeException("Module " + moduleName + " has wrong structure");
            }
        }
    }

    Material parseMaterialType(Map.Entry<String, HashMap<String, Object>> materialTable) {
        String type = (String) materialTable.getValue().get("type");
        switch (type.toLowerCase()) {
            case "solid" -> { return new MaterialSolid(); }
            case "powder" -> { return new MaterialPowder(); }
            case "liquid" -> { return new MaterialLiquid(); }
            case "gas" -> { return new MaterialGas(); }
            default -> { return null; }
        }
    }

    void parseMaterialColors(Map.Entry<String, HashMap<String, Object>> materialTable, Material definition) {
        Object colorObject = materialTable.getValue().get("color");
        if (colorObject != null) {
            ColorWithAplha[] colors = new ColorWithAplha[1];
            colors[0] = new ColorWithAplha(((String) colorObject));
            definition.colors = colors;
        } else if (materialTable.getValue().get("colors") != null) {
            var colorStrings = (List<String>) materialTable.getValue().get("colors");
            ColorWithAplha[] colors = new ColorWithAplha[colorStrings.size()];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new ColorWithAplha(colorStrings.get(i));
            }
            definition.colors = colors;
        }
    }

    void parseMaterialDensity(Map.Entry<String, HashMap<String, Object>> materialTable, Material definition) {
        Object densityObject = materialTable.getValue().get("density");
        if (densityObject != null) {
            definition.density = (Double) densityObject;
        } else {
            definition.density = 0.02f;
        }
    }
//    void updateModuleList()

}
