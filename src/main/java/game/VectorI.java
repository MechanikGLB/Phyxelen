package game;

import java.util.Objects;

public class VectorI {
    int x;
    int y;

    public VectorI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorI vectorI = (VectorI) o;
        return x == vectorI.x && y == vectorI.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
