package game.spells;

import game.*;

import java.util.ArrayList;

public abstract class Spell {
    static String image;
    static String name;
    static float baseCooldown;

    ArrayList<SpellModifier> modifiers = new ArrayList<>();

    /// Returns added cooldown
    public abstract float cast(game.Character caster, Wand wand);
}
