package game;

import static org.lwjgl.opengl.GL21.*;

public class EntityWithCollision extends Entity {
    float collisionBoxWidth;
    float collisionBoxHeight;
    boolean collideWorld = true;
    boolean inAir = true;
    // Velocity
    public float vx = 0;
    public float vy = 0;
    public float gravity = -9.8f;

    public EntityWithCollision(float x, float y, Subworld subworld) {
        super(x, y, subworld);
    }

    @Override
    void update(float dt) {
        if (gravity != 0) {
            vy += gravity;
            var leftPixel = subworld.getPixel(
                    Math.round(x-collisionBoxWidth/2), Math.round(y-collisionBoxHeight/2)-1);
            var middlePixel = subworld.getPixel(
                    Math.round(x), Math.round(y-collisionBoxHeight/2)-1);
            var rightPixel = subworld.getPixel(
                    Math.round(x+collisionBoxWidth/2), Math.round(y-collisionBoxHeight/2)-1);
            inAir = middlePixel.chunk != null
                    && leftPixel.isAir() && rightPixel.isAir() && middlePixel.isAir();
            if (inAir) {
                Pixel castResult = subworld.rayCast(x, y - collisionBoxHeight/2, x, y - collisionBoxHeight/2 + vy * dt);
                if (castResult == null)
                    y += vy * dt;
                else
                    y = castResult.y() + collisionBoxHeight / 2 + 1;
            } else {
                vy = 0;
            }
        }
    }

    @Override
    void draw(float fdt) {
        // TEMP
        glColor3f(0.4f, 0.2f, 0f);
        glBegin(GL_QUADS);
        ((Client)Main.getGame()).renderer.drawRectAtAbsCoordinates(
                x - collisionBoxWidth/2, y - collisionBoxHeight/2,
                x + collisionBoxWidth/2, y + collisionBoxHeight/2
        );
        glEnd();
    }


    @Override
    void move(float dx, float dy) {
        if (!collideWorld) {
            x += dx; y += dy;
            return;
        }
        float verticalEdgeDelta = y + collisionBoxHeight/2 *
                (dy != 0 ? dy/Math.abs(dy) : 0);
        float horizontalEdgeDelta = x + collisionBoxWidth/2 *
                (dx != 0 ? dx/Math.abs(dx) : 0);

        Pixel footPixel = subworld.getPixel(
                Math.round(horizontalEdgeDelta + dx),
                Math.round(y - collisionBoxHeight/2 + dy)
        );
        Pixel aboveFootPixel = subworld.getPixel(
                Math.round(horizontalEdgeDelta + dx),
                Math.round(y - collisionBoxHeight/2 + dy) + 2
        );

        if (!aboveFootPixel.isAir())
            dx = 0;
        else if (!footPixel.isAir()) {
            if (dy < 1)
                dy = 1;
        }
        x += dx; y += dy;
    }
}
