package game;

import game.NetMessage.Message;

import java.util.Random;

/* game.Entity is object that has position in subworld and can be placed not at world
   pixel grid.
*/
public abstract class Entity extends GameObject {
    protected static Random idGenerator = new Random();
    protected float x;
    protected float y;
    protected Subworld subworld;
    protected boolean local = true;
    int id;

    public Entity(float x, float y, Subworld subworld, int id) {
        this.x = x;
        this.y = y;
        this.subworld = subworld;
        this.id = id;
    }

    public Entity(float x, float y, Subworld subworld) {
        this.x = x;
        this.y = y;
        this.subworld = subworld;
        id = idGenerator.nextInt();
    }

    abstract public Message getSpawnMessage();

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public float getX() { return x; };
    public float getY() { return y; };
    public VectorF getPosition() { return new VectorF(x, y); };
    public Subworld getSubworld() { return subworld; };

    void move(float dx, float dy) {
        x += dx; y += dy;
    }
}
