package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class PlayerSpawn extends Message {
    static byte id = 7;
    int x;
    int y;
    int entityId;
    boolean isLocal = false;
    short seed;

    public PlayerSpawn(int x, int y, int id,short seed) {
        this.x = x;
        this.y = y;
        this.entityId = id;
        this.seed = seed;
    }

    public PlayerSpawn(ByteBuffer message) {
        this.entityId = message.getInt();
        System.out.println("Received player "+entityId);
        this.x = message.getInt();
        this.y = message.getInt();
        this.isLocal = message.get() == 1;
        this.seed = message.getShort();

    }

    @Override
    public byte[] toBytes() {
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 3 + 1+ Short.BYTES);
        message.put(id);
        message.putInt(entityId);
        message.putInt(x);
        message.putInt(y);
        message.put((byte) (isLocal ? 1 : 0));
        message.putShort(seed);
        return message.array();
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