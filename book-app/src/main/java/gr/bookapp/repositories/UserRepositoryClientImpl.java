package gr.bookapp.repositories;

import gr.bookapp.client.Client;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.User;
import gr.bookapp.protocol.packages.Response;

import java.util.ArrayList;
import java.util.List;

import static gr.bookapp.protocol.packages.Request.User.*;
import static gr.bookapp.protocol.packages.Response.*;

public final class UserRepositoryClientImpl implements UserRepository {
    private final Client client;

    public UserRepositoryClientImpl(Client client) {
        this.client = client;
    }

    @Override
    public void add(User user) throws InvalidInputException {
        Response response = client.send(new HireEmployeeRequest(user.username(), user.password(), user.role().toString()));
        switch (response) {
            case GeneralSuccessResponse() -> {}
            case ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public void deleteUserByID(long userID) {
        Response response = client.send(new FireEmployeeRequest(userID));
        switch (response) {
            case GeneralSuccessResponse() -> {}
            case ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public User getUserByID(long userID) {
        Response response = client.send(new GetUserByIdRequest(userID));
        return switch (response) {
            case GetUserResponse(User user) -> user;
            case ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public User getUserByUsername(String username) {
        Response response = client.send(new GetUserByUsernameRequest(username));
        return switch (response) {
            case GetUserResponse(User user) -> user;
            case ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<User> getAll() {
        Response response = client.send(new GetAllUsersRequest());
        return switch (response) {
            case GetUsersResponse(List<User> users) -> users;
            case ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public boolean adminExist() {
        Response response = client.send(new AdminExistRequest());
        return switch (response) {
            case GeneralSuccessResponse() -> true;
            case ErrorResponse(String error) -> false;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }


}
