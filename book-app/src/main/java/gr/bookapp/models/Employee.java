package gr.bookapp.models;

public record Employee(long id, String username, String password, Role role) {

    public boolean isAdmin(){ return role == Role.ADMIN; }
}
