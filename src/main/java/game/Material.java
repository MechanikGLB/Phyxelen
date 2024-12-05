package game;

import java.util.Hashtable;

enum MaterialInteractionTarget {
    self,
    other,
    both
}

class MaterialInteraction {
    Object condition = null;
    Material catalyzer = null;
    int catalyzerId = -1;
    MaterialInteractionTarget target = MaterialInteractionTarget.self;
    Material result;
    int resultId = -1;
}


class MaterialBurnRule {
    Material neededMaterial = null;
    int neededMaterialId = -1;
    Color color;
}


public abstract class Material {
    byte id;
    ColorWithAplha[] colors;
    Hashtable<String, MaterialInteraction> interactions;
    Hashtable<Integer, MaterialInteraction> interactionsById;
    double density = 0.02f;

    public ColorWithAplha[] getColors() { return colors; }

    abstract void solvePhysic(Chunk chunk, int i);

    void swap(Chunk c1, int i1, Chunk c2, int i2) {
        Material materialBuffer;
        byte colorBuffer;
        materialBuffer = c1.materials[i1];
        colorBuffer = c1.colors[i1];
        c1.materials[i1] = c2.materials[i2];
        c1.colors[i1] = c2.colors[i2];
        c2.materials[i2] = materialBuffer;
        c2.colors[i2] = colorBuffer;
    }

    void swapColors(Chunk c1, int i1, Chunk c2, int i2) {
        byte colorBuffer;
        colorBuffer = c1.colors[i1];
        c1.colors[i1] = c2.colors[i2];
        c2.colors[i2] = colorBuffer;
    }
}
