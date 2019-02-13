package Server;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(Runnable r,int updater);

    void disconnect(int connectionId);
}
