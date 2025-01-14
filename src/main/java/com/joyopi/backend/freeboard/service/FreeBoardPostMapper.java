package com.joyopi.backend.freeboard.service;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.dto.FreeBoardPostRequestDto;
import com.joyopi.backend.freeboard.dto.FreeBoardPostResponseDto;
import com.joyopi.backend.freeboard.repository.FreeBoardPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FreeBoardPostMapper {

    // FreeBoardPostRequestDto를 FreeBoardPost 도메인 객체로 변환
    FreeBoardPost toPost(FreeBoardPostRequestDto freeBoardPostRequestDto);

    // FreeBoardPost 도메인 객체를 FreeBoardPostEntity로 변환
    @Mapping(source = "userId", target = "user.id") // userId를 user.id로 매핑
    FreeBoardPostEntity toPostEntity(FreeBoardPost freeBoardPost);

    // FreeBoardPostEntity를 FreeBoardPost 도메인 객체로 변환
    @Mapping(source = "user.id", target = "userId") // user.id를 userId로 매핑
    @Mapping(source = "user.nickName", target = "userNickname") // user.nickName을 userNickname으로 매핑
    FreeBoardPost toPost(FreeBoardPostEntity freeBoardPostEntity);

    // List<FreeBoardPostEntity>를 List<FreeBoardPost>로 변환
    List<FreeBoardPost> toPost(List<FreeBoardPostEntity> postEntities);

    // FreeBoardPost 도메인 객체를 FreeBoardPostResponseDto로 변환
    @Mapping(source = "userId", target = "userId") // userId를 userId로 매핑
    @Mapping(source = "userNickname", target = "userNickname") // userNickname을 userNickname으로 매핑
    FreeBoardPostResponseDto toPostResponseDTO(FreeBoardPost freeBoardPost);
}
