package com.example.movieAPI.entities;

import com.example.movieAPI.untils.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    @Email
    private String email;

    private String password;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;


    ///  Return user's authorities
    /// GrantedAuthority is an interface in java spring boot, presents for a particular role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); /// Take role in user role
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
    ///  check expired account
    @Override
    public boolean isAccountNonExpired() {
        return true; /// never expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; /// never lock
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; /// credential of user never expires
    }

    @Override
    public boolean isEnabled() {
        return true; /// enable account such as using email or otp
    }
}
