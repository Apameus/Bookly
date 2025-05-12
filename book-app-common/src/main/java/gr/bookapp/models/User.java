package gr.bookapp.models;

public record User(Long id, String username, String password, Role role) {
    public User(String username, String password, Role role) { this(null, username, password, role);}

    public boolean isAdmin(){ return role == Role.ADMIN; }

    public User withID(Long setID, User user) { return new User(setID, user.username, user.password, user.role); };
}
