package com.plus.service.user.domain;

import com.plus.service.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="PROFILES",
    indexes={
        @Index(name="idx_profile_title", columnList="TITLE"),
        @Index(name="idx_profile_phone_number", columnList="PHONE_NUMBER")
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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=false)
    @ToString.Exclude
    private User user;
}

