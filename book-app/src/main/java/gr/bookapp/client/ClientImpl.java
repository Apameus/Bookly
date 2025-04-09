package gr.bookapp.client;

import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.protocol.packages.ResponseStreamCodec;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public final class ClientImpl implements Client {
    private final RequestStreamCodec requestStreamCodec;
    private final ResponseStreamCodec responseStreamCodec;
    private final SocketAddress socketAddress;

    public ClientImpl(RequestStreamCodec requestStreamCodec, ResponseStreamCodec responseStreamCodec, SocketAddress socketAddress) {
        this.requestStreamCodec = requestStreamCodec;
        this.responseStreamCodec = responseStreamCodec;
        this.socketAddress = socketAddress;
    }

    @Override
    public Response send(Request request) {
        try(Socket socket = new Socket()) {
            socket.connect(socketAddress);
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            requestStreamCodec.serialize(output, request);
            output.flush();

            DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return responseStreamCodec.parse(input);
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}
