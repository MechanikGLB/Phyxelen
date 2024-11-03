package game;

import static org.lwjgl.opengl.GL21.*;

public abstract class HoldableItem extends GameObject {
    Character holder;
    boolean active = false;
    float counter;

    public HoldableItem(Character holder) {
        this.holder = holder;
    }

    @Override
    void update(float dt) {
        counter += dt;
    }

    @Override
    void draw(float fdt) {
        Client client = (Client) Main.getGame();
        glColor3f(0.3f, 0.9f, 0.3f);
        client.renderer.drawRectAtAbsCoordinates(
                3,0,6,2,
                holder.getLookDirection(),
                holder.x, holder.y
        );
    }

    void activate() {
        active = true;
    }

    void deactivate() {
        active = false;
    }
}
