package br.com.alura.AluraFake.user;

import java.io.Serializable;

public class UserListItemDTO implements Serializable {

    private Long id;
    private String name;
    private String email;
    private Role role;

    public UserListItemDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

}
