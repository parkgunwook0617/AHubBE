package ahubbe.ahubbe.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifier;

    @Setter @Getter private String id;

    @Setter @Getter private String password;

    public User() {}

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_animations",
            joinColumns = @JoinColumn(name = "user_identifier"),
            inverseJoinColumns = @JoinColumn(name = "animation_identifier"))
    @Getter
    @Setter
    private List<AnimationInformation> favoriteAnimations = new ArrayList<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }
}
