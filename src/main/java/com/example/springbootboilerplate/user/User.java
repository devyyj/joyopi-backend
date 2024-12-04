package com.example.springbootboilerplate.user;

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
public class User extends BaseEntity {
    private String oauthProvider;
    private String oauthId;
    private String roles;
    private LocalDateTime lastLogin;
}
