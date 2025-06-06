package com.plus.service.profile.domain;

import com.plus.service.global.jpa.BaseTimeEntity;
import com.plus.service.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="PROFILES",
    indexes={
        @Index(name="idx_profile_title", columnList="TITLE"),
        @Index(name="idx_profile_phone_number", columnList="PHONE_NUMBER"),
        @Index(name="idx_profile_created_at", columnList="CREATED_AT"),
    })
@Getter
@Builder
@ToString
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class Profile extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="PROFILE_ID")
    private UUID id;

    @Column(name="TITLE")
    private String title;

    @Column(name="PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @Column(name="PHONE_NUMBER")
    private String phoneNumber;

    @Column(name="ADDRESS")
    private String address;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="IS_DELETED")
    private boolean deleted;

    @Column(name="VIEW_COUNT")
    private int viewCount;
    // TODO: 나중에 user ID로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=false)
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy="profile", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<ProfileView> profileViews = new HashSet<>();

    public void recordView() {
        ProfileView build = ProfileView.builder()
                .profile(this)
                .build();
        profileViews.add(build);
    }
    public void deleted() {
        this.deleted = true;
    }
}

