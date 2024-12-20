package game;

import game.NetMessage.WandCast;

import static org.lwjgl.opengl.GL21.*;

public abstract class HoldableItem extends GameObject {
    public float width;
    public float height;
    private String textureName;
    private Image image;
    Character holder;
    boolean active = false;
    float counter;

    public void setTexture(String textureName) {
        this.textureName = textureName;
    }

    public Image getImage() { return Content.getImage(textureName); }

    public HoldableItem(Character holder) {
        this.holder = holder;
    }

    @Override
    void update(float dt) {
        counter += dt;
    }

    @Override
    void draw(float fdt) {
        if (image == null)
            image = Content.getImage(textureName);
        Client client = (Client) Main.getGame();
        glColor3f(1f, 1f, 1f);
        client.renderer.drawRectAtAbsCoordinates(
                3,0, width, height,
                holder.getLookDirection(),
                holder.x, holder.y, image.getTextureBuffer()
        );
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }
}
