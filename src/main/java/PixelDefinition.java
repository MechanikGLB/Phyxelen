import jdk.jfr.Unsigned;

import java.util.Hashtable;

enum PixelInteractionTarget {
    self,
    other,
    both
}

class PixelInteraction {
    Object condition = null;
    PixelDefinition catalyzer = null;
    int catalyzerId = -1;
    PixelInteractionTarget target = PixelInteractionTarget.self;
    PixelDefinition result;
    int resultId = -1;
}


class PixelBurnRule {
    PixelDefinition neededMaterial = null;
    int neededMaterialId = -1;
    Color color;
}


public class PixelDefinition {
    ColorWithAplha[] colors;
    Hashtable<String, PixelInteraction> interactions;
    Hashtable<Integer, PixelInteraction> interactionsById;
}
