package Server;


import Protocol.DocProtocol;
import Protocol.MessageEncoderDecoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class DocTPC<T> implements Server<T> {

    private final int port;
    private final Supplier<DocProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> readerFactory;
//    private final ActorThreadPool pool;
    private Selector selector;
    private ConnectionsImpl<T> connections;

    private Thread selectorThread;
//    private final ConcurrentLinkedQueue<Runnable> selectorTasks = new ConcurrentLinkedQueue<>();

    public DocTPC(
            int port,
            Supplier<DocProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> readerFactory) {

//        this.pool = new ActorThreadPool(numThreads);
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.readerFactory = readerFactory;
        this.connections = new ConnectionsImpl<T>();
    }

    @Override
    public void serve() {
        selectorThread = Thread.currentThread();
        try  (Selector selector = Selector.open();
             ServerSocketChannel serverSock = ServerSocketChannel.open()) {

            this.selector = selector; //just to be able to close

            serverSock.bind(new InetSocketAddress(port));
            serverSock.configureBlocking(false);
            serverSock.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started");

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("DocTPC start");
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {

                    if (!key.isValid()) {
                        continue;
                    } else if (key.isAcceptable()) {
                        handleAccept(serverSock, selector);
                    } else {
                        handleRead(key);
                    }
                }

                selector.selectedKeys().clear(); //clear the selected keys set so that we can know about new events

            }

        } catch (ClosedSelectorException ex) {
            //do nothing - server was requested to be closed
        } catch (IOException ex) {
            //this is an error
            ex.printStackTrace();
        }

        System.out.println("server closed!!!");
//        pool.shutdown();
    }



    private void handleAccept(ServerSocketChannel serverChan, Selector selector) throws IOException {
        SocketChannel clientChan = serverChan.accept();
        clientChan.configureBlocking(false);
        final BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(
                clientChan,
                readerFactory.get(),
                protocolFactory.get(),
                this,
                connections);
        connections.pushHandler(handler);
        clientChan.register(selector, SelectionKey.OP_READ, handler);
    }

    private void handleRead(SelectionKey key) {
        @SuppressWarnings("unchecked")
        BlockingConnectionHandler<T> handler = (BlockingConnectionHandler<T>) key.attachment();

        if (key.isReadable()) {
            Runnable task = handler.continueRead();
            if (task != null) {
                handler.submit(task);
            }
        }
    }

    private void distributeUpdates(){

    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

}
