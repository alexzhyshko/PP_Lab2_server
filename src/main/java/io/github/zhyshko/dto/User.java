package io.github.zhyshko.dto;

import java.util.Objects;
import java.util.UUID;

public class User {

    private String username;
    private UUID id;

    public User() {
    }

    public User(String username, UUID id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(id, other.id) && Objects.equals(username, other.username);
    }

    @Override
    protected Object clone() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        return user;
    }



}
