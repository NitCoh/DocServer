package Protocol;
import Server.ConnectionHandler;
import Server.Server;

public interface Message {

    void process(Server server, ConnectionHandler handler);
    byte[] encodeMe();
}
