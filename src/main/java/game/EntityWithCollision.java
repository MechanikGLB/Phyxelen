package game;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL21.*;

public class EntityWithCollision extends Entity {
    float collisionBoxWidth;
    float collisionBoxHeight;
    boolean collideWorld = true;
    boolean collidable = false;
    boolean inAir = true;
    // Velocity
    public float vx = 0;
    public float vy = 0;
    public float gravity = -9.8f;

    public EntityWithCollision(float x, float y, Subworld subworld, boolean collidable) {
        super(x, y, subworld);
        this.collidable = collidable;
    }

    @Override
    void update(float dt) {
        if (gravity != 0) {
            vy += gravity * dt * 30;
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
    void draw(float fdt) {}


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

    boolean intersectedByRay(float x1, float y1, float x2, float y2) {
        // From https://noonat.github.io/intersect/
        float scaleX = 1.0f / (x2-x1);
        float scaleY = 1.0f / (y2-y1);
        float signX = scaleX < 0 ? -1: 1;
        float signY = scaleY < 0 ? -1: 1;
        float nearTimeX = (x - signX * (collisionBoxWidth / 2) - x1) * scaleX;
        float nearTimeY = (y - signY * (collisionBoxHeight / 2) - y1) * scaleY;
        float farTimeX = (x + signX * (collisionBoxWidth / 2) - x1) * scaleX;
        float farTimeY = (y + signY * (collisionBoxHeight / 2) - y1) * scaleY;

        if (nearTimeX > farTimeY || nearTimeY > farTimeX) {
            return false;
        }

        float nearTime = max(nearTimeX, nearTimeY);
        float farTime = min(farTimeX, farTimeY);

        if (nearTime >= 1 || farTime <= 0) {
            return false;
        }

        // We don't need this now
//        float hitTime = clamp(nearTime, 0, 1);
////        if (nearTimeX > nearTimeY) {
////            hit_normal.x = -signX;
////            hit_normal.y = 0;
////        } else {
////            hit_normal.x = 0;
////            hit_normal.y = -signY;
////        }
////        hit.delta.x = (1.0 - hit.time) * -delta.x;
////        hit.delta.y = (1.0 - hit.time) * -delta.y;
//        float hitX = x1 + (x1-x2) * hitTime;
//        float hitY = y1 + (y1-y2) * hitTime;
//        return hit;
        return true;
        // try https://tavianator.com/2015/ray_box_nan.html one day
    }
}
