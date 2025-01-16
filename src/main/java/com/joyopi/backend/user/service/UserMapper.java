package com.joyopi.backend.user.service;

import com.joyopi.backend.user.domain.User;
import com.joyopi.backend.user.dto.UserResponseDto;
import com.joyopi.backend.user.repository.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // UserEntity → User 변환
    @Mapping(source = "id", target = "id")
    User toDomain(UserEntity entity);

    // User → UserEntity 변환
    @Mapping(source = "id", target = "id")
    UserEntity toEntity(User user);

    // User → UserResponseDto 변환
    UserResponseDto toResponseDto(User user);
}
