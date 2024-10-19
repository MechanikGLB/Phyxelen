public class Character extends EntityWithCollision {
    public boolean canGo = true;

    public Character(float x, float y, Subworld subworld) {
        super(x, y, subworld);
    }

    void go(float dx, float dy) {
        if (canGo)
            move(dx, dy);
    }
}
