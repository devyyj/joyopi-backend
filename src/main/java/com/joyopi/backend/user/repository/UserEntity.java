package com.joyopi.backend.user.repository;

import com.joyopi.backend.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauthProvider", "oauthId"})
        }
)
public class UserEntity extends BaseEntity {
    private String nickName;
    private String oauthProvider;
    private String oauthId;
    private String role;
    private LocalDateTime lastLogin;
}
