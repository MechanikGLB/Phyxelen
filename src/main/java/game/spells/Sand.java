package game.spells;

import game.*;
import game.Character;

public class Sand extends Spell {
    public Sand() {
        name = "Sand";
        image = "sand_spell.png";
    }

    @Override
    public float cast(Character caster, Wand wand) {
        var subworld = caster.getSubworld();
        var material = Content.getMaterial("sand");
        var colorId = (byte) subworld.random().nextInt(material.getColors().length);
        var color = material.getColors()[colorId];
        Projectile projectile = new Projectile(
                wand.getCastX(), wand.getCastY(), subworld, caster,
                (self, o) -> {
                    if (o instanceof Pixel && !(((Pixel) o).material() instanceof MaterialAir)) {
                        subworld.setPixel(
                                Math.round(self.getX()), Math.round(self.getY()), material, colorId);
                        return true;
                    }
                    return false;
                },
                (float) Math.cos(caster.getLookDirection()) * 100,
                (float) Math.sin(caster.getLookDirection()) * 100,
                0f, -9.8f,
                (short) 1, (short) 1, false, null, color);

        subworld.addEntity(projectile);
        return 0.07f;
    }
}
