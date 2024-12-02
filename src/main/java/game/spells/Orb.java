package game.spells;

import game.*;
import game.Character;

public class Orb extends Spell {

    public Orb() {
        name = "Small orb";
        projectileImage = "medium_projectile.png";
        image = "orb_spell.png";
    }

    @Override
    public float cast(Character caster, Wand wand) {
        var subworld = caster.getSubworld();
        Projectile projectile = new Projectile(
                wand.getCastX(), wand.getCastY(), subworld,
                (self, o) -> {
                    if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
                        subworld.removeEntity(self);
                                subworld.fillPixels(
                                        ((Pixel)o).x() - 2, ((Pixel)o).y() - 2, 4, 4,
                                        Content.air(), (byte) 0);
                                return true;
                    }
                    return false;
                },
                (float) Math.cos(caster.getLookDirection()) * 100,
                (float) Math.sin(caster.getLookDirection()) * 100,
                0f, 0f,
                (short) 6, (short) 6, true, "medium_projectile.png", ColorWithAplha.white());

        subworld.addEntity(projectile);
        return 0.5f;
    }
}
