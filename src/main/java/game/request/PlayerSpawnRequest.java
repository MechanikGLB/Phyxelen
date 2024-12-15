package game.request;

import game.Connection;
import game.Main;
import game.NetMessage.ChunkSync;
import game.NetMessage.PlayerSpawn;
import game.Player;
import game.VectorI;

public class PlayerSpawnRequest extends Request {
    VectorI indexes;

    public  PlayerSpawnRequest(Connection receiver) {
        super(receiver);
    }

    @Override
    public void process() {
        var subworld = Main.getGame().getActiveSubworld();
        Player player = new Player(0,0,subworld, receiver);
        subworld.spawnPlayer(player);
//        receiver.addMessage(player.getSpawnMessage());


    }
}
