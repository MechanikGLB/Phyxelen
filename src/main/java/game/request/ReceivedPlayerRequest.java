package game.request;

import game.Client;
import game.Connection;
import game.Main;
import game.NetMessage.Initialized;
import game.NetMessage.RequestEntities;
import game.Player;

public class ReceivedPlayerRequest extends Request {
    int x;
    int y;
    int entityId;
    short seed;

    public ReceivedPlayerRequest(Connection receiver, int x, int y, int entityId, short seed) {
        super(receiver);
        this.x = x;
        this.y = y;
        this.entityId = entityId;
        this.seed = seed;
    }

    @Override
    public void process() {
        var client = (Client)Main.getGame();

        var subworld = Main.getGame().getActiveSubworld();
        var players = subworld.getPlayers();
        for (var player : players)
            if (player.getId() == entityId) {
                System.out.println("Doesn't spawn player "+entityId+" as it already exist");
                return;
            }
        System.out.println("Spawns player "+entityId);

        Player playerToSpawn = new Player(x, y, subworld, null);
        playerToSpawn.setLocal(false);
        if(client.getPrimaryCharacter() == null){
            client.setPrimaryCharacter(playerToSpawn);
            Main.getClient().addMessage(new Initialized());
            Main.getClient().addMessage(new RequestEntities());
        }
        players.add(playerToSpawn);
        subworld.addEntity(playerToSpawn);

        playerToSpawn.setId(entityId);
        playerToSpawn.spawn(x,y,seed);
    }
}
