package game.spells;

import game.Character;
//import game.ColorWithAplha;
//import game.Projectile;
import game.*;

public class Bullet extends Spell {
    public Bullet() {
        name = "Bullet";
        projectileImage = "bullet.png";
        image = "bullet_spell.png";
    }

    @Override
    public float cast(Character caster, Wand wand) {
        var subworld = caster.getSubworld();
        Projectile projectile = new Projectile(
                wand.getCastX(), wand.getCastY(), subworld,
                (self, o) -> {
                    if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
                        ((Pixel) o).chunk().setPixel(((Pixel) o).i(), Content.air(), (byte) 0);
                        return true;
                    }
                    return false;
                },
                (float) Math.cos(caster.getLookDirection()) * 200,
                (float) Math.sin(caster.getLookDirection()) * 200,
                0f, -9.8f,
                (short) 3, (short) 1, true, "bullet.png", ColorWithAplha.white());

        subworld.addEntity(projectile);
        return 0.3f;
    }
}
