package game;

/* game.Entity is object that has position in subworld and can be placed not at world
   pixel grid.
*/
public abstract class Entity extends GameObject {
    protected float x;
    protected float y;
    protected Subworld subworld;

    public Entity(float x, float y, Subworld subworld) {
        this.x = x;
        this.y = y;
        this.subworld = subworld;
    }

    public float getX() { return x; };
    public float getY() { return y; };
    public VectorF getPosition() { return new VectorF(x, y); };
    public Subworld getSubworld() { return subworld; };

    void move(float dx, float dy) {
        x += dx; y += dy;
    }
}
