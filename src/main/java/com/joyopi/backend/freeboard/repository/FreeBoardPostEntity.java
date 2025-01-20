package com.joyopi.backend.freeboard.repository;


import com.joyopi.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    private Long userId;

    private String userNickname;
}
