package game;

abstract public class GameObject {
    ///@param dt Delta time from last update
    abstract void update(float dt);
    ///@param fdt Delta time from last frame
    abstract void draw(float fdt);
}
