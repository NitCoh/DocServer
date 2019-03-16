package Server;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg,T updaterMsg,int updater);

    void disconnect(int connectionId);
}
