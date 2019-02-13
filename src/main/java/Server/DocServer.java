package Server;

import Protocol.DocProtocol;
import Protocol.MessageEncoderDecoder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public abstract class DocServer<T> implements Server<T> {
    private final int port;
    private final Supplier<DocProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory;
    private ServerSocket servSocket=null;
    private Connections<T> connections;
    // Main Shared-Object
    private String doc;
    ConcurrentLinkedQueue<Runnable> distirbuteDiffsQueue;

    public DocServer(int port, Supplier<DocProtocol<T>> protocolFactory,
                     Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) {
        this.port = port;
        this.protocolFactory= protocolFactory;
        this.encoderDecoderFactory=encoderDecoderFactory;
        connections=new ConnectionsImpl<T>();
        doc=new String();
        distirbuteDiffsQueue=new ConcurrentLinkedQueue<>();
    }
    public void serve() {
        try (ServerSocket servSocket = new ServerSocket(port)) { //Try with resources
            this.servSocket = servSocket;

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Server is listening to port: "+port);
                Socket clientSocket = servSocket.accept();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(clientSocket,
                        encoderDecoderFactory.get(), protocolFactory.get(), connections);
                ((ConnectionsImpl)connections).pushHandler(handler);
            }
        }
        catch(IOException e){
            }

        }

    public void close() throws IOException{
        if(servSocket!=null)
            servSocket.close();
    }
    protected abstract void execute(BlockingConnectionHandler<T> handler);
    };
