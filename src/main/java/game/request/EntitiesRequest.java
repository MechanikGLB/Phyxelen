package game.request;

import game.Connection;
import game.Main;
import game.NetMessage.ChunkSync;
import game.Player;

public class EntitiesRequest extends Request {

    public  EntitiesRequest(Connection receiver) {
        super(receiver);
    }

    @Override
    public void process() {
        var subworld = Main.getGame().getActiveSubworld();
        subworld.
        subworld.loadChunk(indexes);
        receiver.addMessage(new ChunkSync(subworld.getActiveChunk(indexes)));
    }
}
