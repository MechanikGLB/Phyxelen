import java.util.function.Consumer;
import static org.lwjgl.opengl.GL21.*;

public class HoldableItem extends GameObject {
    Consumer<HoldableItem> onActivate;
    Consumer<HoldableItem> onUpdate;
    Consumer<HoldableItem> onDeactivate;
    Character holder;
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
//        glBegin(GL_QUADS);
        client.renderer.drawRectAtAbsCoordinates(
//                holder.x + (float) Math.cos(holder.getLookDirection()) * 1,
//                holder.y + (float) Math.sin(holder.getLookDirection()) * 1,
//                holder.x + (float) Math.cos(holder.getLookDirection()) * 1 + 4,
//                holder.y + (float) Math.sin(holder.getLookDirection()) * 1 + 2,
                3,
                0,
                6,
                2,
//                -2,-4,2,4,
                holder.getLookDirection(),
                holder.x, holder.y
        );
//        glEnd();
        glBegin(GL_LINES);
        glVertex2f(client.renderer.screenWidth / 2,client.renderer.screenHeight / 2);
        glVertex2f(client.renderer.screenWidth / 2 + (float)Math.cos(holder.getLookDirection()) * 40,
                client.renderer.screenHeight / 2 + (float)Math.sin(holder.getLookDirection()) * 40
        );
        glEnd();
//        System.out.println(holder.getLookDirection());
    }
}
