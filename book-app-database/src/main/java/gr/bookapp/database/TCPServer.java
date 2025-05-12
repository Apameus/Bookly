package gr.bookapp.database;

import gr.bookapp.log.Logger;
import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.protocol.packages.ResponseStreamCodec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public final class TCPServer implements AutoCloseable {
    private final Controller controller;
    private final ServerSocket serverSocket;
    private final RequestStreamCodec requestStreamCodec;
    private final ResponseStreamCodec responseStreamCodec;
    private final Logger logger;

    public TCPServer(SocketAddress socketAddress, Logger.Factory logFactory, Controller controller, RequestStreamCodec requestCodec) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(socketAddress);

        this.controller = controller;
        this.requestStreamCodec = requestCodec;
        this.responseStreamCodec = new ResponseStreamCodec();

        logger = logFactory.create("TCP_Server");
    }

    public void run() throws IOException {
        while (true){
            try (Socket socket = serverSocket.accept();
                 DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                 DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            ) {

                logger.log("Connection established from " + socket.getInetAddress());

                Request request = requestStreamCodec.parse(dataInputStream);

                logger.log("Got request: " + request);

                Response response = controller.handleRequest(request);

                responseStreamCodec.serialize(dataOutputStream, response);
                dataOutputStream.flush();

                logger.log("Sending response: " + response);
            }
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }
}
