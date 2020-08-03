package com.example.engine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Completed> completed;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "User must have an email")
    //@Email
    @Pattern(regexp = "^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$")
    private String email;

    @Size(min=5, message = "At least 5 characters")
    private String password;

    public User() { }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    @JsonProperty(value = "authorities")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @JsonProperty(value = "username")
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @JsonProperty(value = "accountNonExpired")
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @JsonProperty(value = "accountNonLocked")
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @JsonProperty(value = "credentialsNonExpired")
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @JsonProperty(value = "enabled")
    @Override
    public boolean isEnabled() {
        return true;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public List<Completed> getCompleted() {
        return completed;
    }

    public void setCompleted(List<Completed> completed) {
        this.completed = completed;
    }
}
