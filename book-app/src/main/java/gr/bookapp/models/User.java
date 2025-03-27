package gr.bookapp.models;

public record User(long id, String username, String password, Role role) {

    public boolean isAdmin(){ return role == Role.ADMIN; }
}
