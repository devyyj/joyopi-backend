package com.joyopi.backend.freeboard.repository;


import com.joyopi.backend.common.entity.BaseEntity;
import com.joyopi.backend.user.repository.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "freeboard_posts")
@EqualsAndHashCode(callSuper = true)
public class FreeBoardPostEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
