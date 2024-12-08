package game;

import game.spells.Spell;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Wand extends HoldableItem {
//    game.Projectile projectile;
    ArrayList<Spell> spells = new ArrayList<>();
    /// Time before will be able to cast, in seconds
    float cooldown = 0;

    public Wand(Character holder) {
        super(holder);
        width = 20;
        height = 11;
    }

    public float getCastX() {
        return holder.x + (float) Math.cos(holder.getLookDirection()) * 8;
    }
    public float getCastY() {
        return holder.y + (float) Math.sin(holder.getLookDirection()) * 8;
    }

    public ArrayList<Spell> getSpells() { return spells; }

    @Override
    void activate() {
        super.activate();
        if (cooldown <= 0)
            cast();
    }

    @Override
    void update(float dt) {
        super.update(dt);
        if (cooldown > 0)
            cooldown -= dt;
        if (!active || spells.isEmpty())
            return;
        if (cooldown <= 0)
            cast();
    }

    public void cast() {
        for (var spell : spells)
            cooldown += spell.cast(holder, this);
    }
}
