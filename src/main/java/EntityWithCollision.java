import static org.lwjgl.opengl.GL21.*;

public class EntityWithCollision extends Entity {
    float collisionBoxWidth;
    float collisionBoxHeight;
    boolean collideWorld = true;

    public EntityWithCollision(float x, float y, Subworld subworld) {
        super(x, y, subworld);
    }

    @Override
    void update(float dt) {

    }

    @Override
    void draw(float fdt) {
        // TEMP
        glColor3f(0.2f, 0.0f, 0f);
        glBegin(GL_QUADS);
        ((Client)Main.getGame()).renderer.drawRectAtAbsCoordinates(
                x - collisionBoxWidth/2, y - collisionBoxHeight/2,
                x + collisionBoxWidth/2, y + collisionBoxHeight/2
        );
        glEnd();
    }


    @Override
    void move(float dx, float dy) {
        //TODO: implement collision
        /*TEMP*/x += dx; y += dy;
    }
}
