package Protocol;

import Server.ConnectionHandler;
import Server.Connections;

public interface DocProtocol<T>  {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);
    
    void process(T message, ConnectionHandler handler);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
