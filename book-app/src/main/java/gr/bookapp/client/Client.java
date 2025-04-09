package gr.bookapp.client;

import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.Response;

public interface Client {
    Response send(Request request);
}
