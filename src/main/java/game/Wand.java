package game;

import java.util.function.Consumer;

public class Wand extends HoldableItem {
//    game.Projectile projectile;


    public Wand(Consumer<HoldableItem> onActivate, Consumer<HoldableItem> onUpdate, Consumer<HoldableItem> onDeactivate,
                Character holder
    ) {
        super(holder);
    }
}
