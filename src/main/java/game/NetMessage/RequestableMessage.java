package game.NetMessage;


abstract public class RequestableMessage extends Message {
    abstract public Message makeMessageByRequest();
}
