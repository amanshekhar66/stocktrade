package stocktrade.stocktrade.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import stocktrade.stocktrade.enums.Permissions;
import stocktrade.stocktrade.enums.Roles;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_details",
       indexes = {@Index(name = "userEmail_idx",columnList = "userEmail"),
                  @Index(name = "userId_idx",columnList = "userId")})
public class UserDetailsEntity extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String userEmail;
    private String userPassword;
    private String firstName;
    private String surName;
    private LocalDate DOB;
    private String gender;
    private Boolean isVerified;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles = new HashSet<>();

//    @Enumerated(EnumType.STRING)
//    private Permissions permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name())));
//        authorities.add(new SimpleGrantedAuthority(permissions.name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }
}
