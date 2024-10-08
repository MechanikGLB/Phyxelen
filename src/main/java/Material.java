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
    ColorWithAplha[] colors;
    Hashtable<String, MaterialInteraction> interactions;
    Hashtable<Integer, MaterialInteraction> interactionsById;
    double density = 0.02f;

    abstract void solvePhysic(Subworld subworld, Pixel pixel);
}
