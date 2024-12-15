package game.request;

import game.Connection;

public abstract class Request {
    Connection receiver;

    public Request(Connection receiver) {
        this.receiver = receiver;
    }

    abstract public void process();
}
