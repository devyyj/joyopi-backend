package com.example.springbootboilerplate.user.repository;

import com.example.springbootboilerplate.common.entity.BaseEntity;
import jakarta.persistence.*;
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
    private String oauthProvider;
    private String oauthId;
    private String role;
    private LocalDateTime lastLogin;
}
