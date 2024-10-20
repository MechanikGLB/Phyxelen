import java.util.function.Consumer;
import static org.lwjgl.opengl.GL21.*;

public class HoldableItem extends GameObject {
    Consumer<HoldableItem> onActivate;
    Consumer<HoldableItem> onUpdate;
    Consumer<HoldableItem> onDeactivate;
    Character holder;
    boolean active = false;
    float counter;

    public HoldableItem(Consumer<HoldableItem> onActivate, Consumer<HoldableItem> onUpdate, Consumer<HoldableItem> onDeactivate, Character holder) {
        this.onActivate = onActivate;
        this.onUpdate = onUpdate;
        this.onDeactivate = onDeactivate;
        this.holder = holder;
    }

    @Override
    void update(float dt) {
        if (onUpdate != null)
            onUpdate.accept(this);
    }

    @Override
    void draw(float fdt) {
        Client client = (Client) Main.getGame();
        client.renderer.drawRectAtAbsCoordinates(
                3,0,6,2,
                holder.getLookDirection(),
                holder.x, holder.y
        );
        glBegin(GL_LINES);
        glVertex2f(client.renderer.screenWidth / 2,client.renderer.screenHeight / 2);
        glVertex2f(client.renderer.screenWidth / 2 + (float)Math.cos(holder.getLookDirection()) * 40,
                client.renderer.screenHeight / 2 + (float)Math.sin(holder.getLookDirection()) * 40
        );
        glEnd();
    }

    void activate() {
        active = true;
        if (onActivate != null)
            onActivate.accept(this);
    }

    void deactivate() {
        active = false;
        if (onDeactivate != null)
            onDeactivate.accept(this);
    }
}
