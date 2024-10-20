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
        if (onUpdate != null) {
            onUpdate.accept(this);
            counter += dt;
        }
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
        if (onActivate != null)
            onActivate.accept(this);
    }

    void deactivate() {
        active = false;
        if (onDeactivate != null)
            onDeactivate.accept(this);
    }
}
