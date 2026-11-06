package com.schooltech.sms.entity.client.user;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user")
@Where(clause = "is_active=true")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", unique = true, nullable = false)  //username will be treated as User_id
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_no", unique = true, nullable = false)
    private String phoneNo;

    @Column(name = "alt_phone_no")
    private String altPhoneNo;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "password", nullable = false)
    private String password;


    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
    //--------------------------Entity Ends--------------------------------

    @Transient
    private String otp;  //this will be used when we send otp to update user. It will be transient as it wont be saved to db

    //Below are the properties for spring Authentication
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role);
        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
