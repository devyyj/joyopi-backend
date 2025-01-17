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

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;
}
