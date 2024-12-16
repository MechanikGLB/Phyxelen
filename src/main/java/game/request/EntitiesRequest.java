package game.request;

import game.Connection;
import game.Entity;
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
        var entities = subworld.getEntities();

        receiver.setInitialized(true);
        receiver.addMessage(entities.getFirst().getSpawnMessage());
        for (Entity entity : entities) {
            System.out.println("Will send entity " + entity.getId());
            receiver.addMessage(entity.getSpawnMessage());
        }
    }
}
