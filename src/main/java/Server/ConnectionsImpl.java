package Server;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private static int idToInsert = 0;
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> activeClients = new ConcurrentHashMap<>();

    public void pushHandler(ConnectionHandler<T> toPush) {
        activeClients.put(idToInsert, toPush);
        idToInsert++;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler <T> connectionHandler = this.activeClients.getOrDefault(connectionId, null);
        if (connectionHandler != null && msg !=null) {
            connectionHandler.send(msg);
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void broadcast(Runnable r, int updater) {
            for (ConnectionHandler<T> connectionHandler : activeClients.values()) {
                if(((BlockingConnectionHandler)connectionHandler).getMyID()!=updater)
                    ((BlockingConnectionHandler<T>) connectionHandler).submit(()->{
                        int tofill=0;
                    });
                    else
                        ((BlockingConnectionHandler<T>) connectionHandler).submit(()->{
                            int tofill=0;
                        });

            }
    }

    @Override
    public void disconnect(int connectionId) {
        activeClients.remove(connectionId);
    }
}
