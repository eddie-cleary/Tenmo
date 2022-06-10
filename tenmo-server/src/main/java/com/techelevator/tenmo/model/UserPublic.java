package com.techelevator.tenmo.model;

// UserPublic is created to show only username and id of user. If only User class were used, it would expose sensitive
// information like password hashes and database structure
public class UserPublic {
    private Long id;
    private String username;

    public UserPublic() { }

    public UserPublic(Long id, String username){
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
