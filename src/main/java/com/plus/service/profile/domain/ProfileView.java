package com.plus.service.profile.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="PROFILES_VIEWS",
    indexes={
        @Index(name="idx_profile_viewed_at", columnList="VIEWED_AT"),
        @Index(name="idx_profile_viewer_ip", columnList="VIEWER_IP")
    })
@Getter
@Builder
@ToString
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class ProfileView {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="PROFILE_ID")
    private Profile profile;

    @Column(name="VIEWED_AT")
    @CreatedDate
    private LocalDateTime viewedAt;

}
