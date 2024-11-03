package game;

import static org.lwjgl.opengl.GL20.*;

public class PixelEntity extends Projectile {
    Material material;
    byte color;


    public PixelEntity(float x, float y, Subworld subworld, Material material, byte color) {
        super(x, y, subworld, (self, o) -> {
            if (o instanceof Pixel && !(((Pixel) o).chunk.materials[((Pixel) o).i] instanceof MaterialAir)) {
                subworld.setPixel(Math.round(self.x), Math.round(self.y), material, color);
                subworld.removeEntity(self);
                return true;
            }
            else return false;
        });
        this.material = material;
        this.color = color;
    }
    public PixelEntity(
            float x, float y, Subworld subworld, Material material, byte color,
            float velocityX, float velocityY,
            float accelerationX, float accelerationY) {
        this(x, y, subworld, material, color);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }


    @Override
    void draw(float fdt) {
        // TODO: waits for new world pixel implementation merge
        ColorWithAplha color = material.colors[this.color];
        // TEMP?
        glColor4f(color.r, color.g, color.b, color.alpha);
        glBegin(GL_QUADS);
        ((Client)Main.getGame()).renderer.drawRectAtAbsCoordinates(x, y, x+1, y+1);
        glEnd();
    }
}
