package game.spells;

import game.Character;
import game.*;

public class ExplosiveOrb extends Spell {

    public ExplosiveOrb() {
        name = "Explosive orb";
        projectileImage = "explosive_orb.png";
        image = "explosive_orb_spell.png";
    }

    @Override
    public float cast(Character caster, Wand wand) {
        var subworld = caster.getSubworld();
        Projectile projectile = new Projectile(
                wand.getCastX(), wand.getCastY(), subworld, caster,
                (self, o) -> {
                    if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
                        ((Pixel) o).chunk().setPixel(((Pixel) o).i(), Content.air(), (byte) 0);
                        subworld.jetPixels(((Pixel)o).x(), ((Pixel)o).y(), 16);
                        return true;
                    } else if (o instanceof Character) {
                        ((Character)o).damage(50);
                        return true;
                    }
                    return false;
                },
                (float) Math.cos(caster.getLookDirection()) * 70,
                (float) Math.sin(caster.getLookDirection()) * 70,
                0f, -3f,
                (short) 6, (short) 6, true, projectileImage, ColorWithAplha.white());

        subworld.addEntity(projectile);
        return 2f;
    }
}
