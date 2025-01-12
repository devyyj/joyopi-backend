package com.joyopi.backend.freeboard.service;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.repository.FreeBoardPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FreeBoardPostMapper {

    // PostEntity를 Post 도메인 객체로 변환
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickName", target = "userNickname")
    FreeBoardPost toPost(FreeBoardPostEntity freeBoardPostEntity);

    // List<PostEntity> -> List<Post> 변환
    List<FreeBoardPost> toPost(List<FreeBoardPostEntity> postEntities);

    // Post 도메인 객체를 PostEntity로 변환
    @Mapping(source = "userId", target = "user.id")
    FreeBoardPostEntity toPostEntity(FreeBoardPost freeBoardPost);
}