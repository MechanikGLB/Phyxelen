package game.NetMessage;

import game.Connection;

import java.nio.ByteBuffer;

abstract public class Message {
    public Connection senderConnection; /// Valid on server only

    public abstract byte[] toBytes();
    public abstract void process();

    /// Makes message for putting into queue in client
    public static Message make(ByteBuffer bytes) {
        return make(bytes, null);
    }

    /// Makes message for putting into queue in server
    public static Message make(ByteBuffer bytes, Connection sender) {
        Message newMessage = switch (bytes.get()) {
            case 0 -> new Hello(bytes);
            case 1 -> new Quit();
            case 2 ->  new RequestContent();
            case 3 ->  new ContentSync(bytes);
            case 4 ->  new RequestChunk(bytes);
            case 5 ->  new ChunkSync(bytes);
            case 6 ->  new RequestPlayerSpawn();
            case 7 ->  new PlayerSpawn(bytes);
            case 8 ->  new ProjectileSpawn(bytes);
            case 9 ->  new RequestEntities();
            case 10 ->  new PlayerMovementSync(bytes);
            case 11 ->  new PlayerHeldItemSync(bytes);
            case 12 ->  new Initialized();
            case 13 ->  new PlayerHealthSync(bytes);
            case 14 ->  new PlayerRespawnSync(bytes);
            case 15 ->  new WandCast(bytes);
            default -> throw new RuntimeException("Unknown message type: "+bytes.get(0));
        };
        newMessage.senderConnection = sender;
//        newMessage.message = bytes; // Buffer pointer == 1 here
        return newMessage;
    }
}