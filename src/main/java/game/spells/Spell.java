package game.spells;

import game.*;

import java.util.ArrayList;

public abstract class Spell {
    String projectileImage;
    String image;
    String name;
    float baseCooldown;

    ArrayList<SpellModifier> modifiers = new ArrayList<>();

    public String getImage() { return image; }

    /// Returns added cooldown
    public abstract float cast(game.Character caster, Wand wand);
}
