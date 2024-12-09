package game.spells;

import game.Character;
import game.*;

public class WaterOrb extends Spell {

    public WaterOrb() {
        name = "Water orb";
        projectileImage = "water_orb.png";
        image = "water_orb_spell.png";
    }

    @Override
    public float cast(Character caster, Wand wand) {
        var subworld = caster.getSubworld();
        Projectile projectile = new Projectile(
                wand.getCastX(), wand.getCastY(), subworld,
                (self, o) -> {
                    if (o instanceof Pixel && !((Pixel) o).isAir()) {
                        subworld.removeEntity(self);
                                subworld.fillPixels(
                                        ((Pixel)o).x() - 3, ((Pixel)o).y() - 3, 6, 6,
                                        Content.getMaterial("water"), (byte) -1, 1);
                                return true;
                    }
                    return false;
                },
                (float) Math.cos(caster.getLookDirection()) * 70,
                (float) Math.sin(caster.getLookDirection()) * 70,
                0f, -3f,
                (short) 6, (short) 6, true, projectileImage, ColorWithAplha.white());

        subworld.addEntity(projectile);
        return 0.7f;
    }
}
