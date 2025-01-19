package com.joyopi.backend.user.repository;

import com.joyopi.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?") // DELETE 대신 UPDATE 실행
@SQLRestriction("deleted = false") // 조회 시 삭제된 데이터 제외
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"oauthProvider", "oauthId"})
        }
)
public class UserEntity extends BaseEntity {

    @Column(length = 20)
    private String nickName;

    private String oauthProvider;
    private String oauthId;
    private String role;
    private LocalDateTime lastLogin;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false; // 소프트 삭제 플래그
}
