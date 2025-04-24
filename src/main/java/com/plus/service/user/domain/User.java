package com.plus.service.user.domain;

import com.plus.service.global.jpa.BaseTimeEntity;
import com.plus.service.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="USERS",
    indexes={
        @Index(name="idx_user_email", columnList="EMAIL"),
        @Index(name="idx_user_username", columnList="USERNAME")
    })
@Getter
@Builder
@ToString
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="USER_ID")
    private UUID id;

    @Column(name="EMAIL", unique=true)
    private String email;

    @Column(name="USERNAME", unique=true)
    private String username;

    @Column(name="PASSWORD")
    private String encodedPassword;

    @Column(name="IS_DELETED")
    private boolean deleted;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE")
    @Builder.Default
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true)
    @ToString.Exclude
    @Builder.Default
    private Set<Profile> profiles = new HashSet<>();

    public void deleted(){
        this.deleted = true;
    }
}
