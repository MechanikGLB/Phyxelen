package game.NetMessage;

import game.*;

import java.nio.ByteBuffer;

public class PlayerHealthSync extends Message {

    static byte id = 13;
    int entityId;
    int health;

    public PlayerHealthSync(int entityId,int health) {
        this.entityId = entityId;
        this.health = health;
    }

    public PlayerHealthSync(ByteBuffer message) {
        this.entityId = message.getInt();
        this.health = message.getInt();
    }

    @Override
    public byte[] toBytes() {
        System.out.println("Will Damage player ["+entityId+"] to ["+health+"] health");
        ByteBuffer message = ByteBuffer.allocate(1 + Integer.BYTES * 2);
        message.put(id);
        message.putInt(entityId);
        message.putInt(health);
        return message.array();
    }

    @Override
    public void process() {
//        var state = Main.getGame().getGameState();
//        var players = Main.getGame().getActiveSubworld().getPlayers();
//        if (state == GameApp.GameState.Server){
//            Connection playerToConfirm = Main.getServer().getCurrentConnection();
//            for (var player : players)
//                if (player.getId() == entityId) {
////                    if (player.getHealth() != health) {
//                    System.out.println("[SERVER] Player [ " + entityId + "][" + player.getHealth() +" ] health to sync: " + health );
//                    Main.getServer().broadcastMessage(new PlayerHealthSync(entityId, player.getHealth()));
////                    } else {
////                        System.out.println("[SERVER] Player [ " + entityId + " ] health already synced");
////                        Main.getServer().broadcastMessage(new PlayerHealthSync(entityId, ))
////                    }
////
//                }
//        } else {
//            for (var player : players)
//                if (player.getId() == entityId) {
//                    if (player.getHealth() != health) {
//                        player.setHealth(health);
//                        System.out.println("Unsynced Player [ " + entityId + " ] health synced");
//                    } else { System.out.println("Player [ " + entityId + " ] health already synced");}
//                }
//        }
    }

}
