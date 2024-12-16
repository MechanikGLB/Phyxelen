package game.request;

import game.Connection;
import game.Main;
import game.NetMessage.ChunkSync;
import game.VectorI;

public class ChunkRequest extends Request {
    VectorI indexes;

    public ChunkRequest(Connection receiver, int x, int y) {
        super(receiver);
        indexes = new VectorI(x, y);
    }

    @Override
    public void process() {

    }
}
