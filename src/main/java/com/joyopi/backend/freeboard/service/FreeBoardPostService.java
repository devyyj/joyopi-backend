package com.joyopi.backend.freeboard.service;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.dto.FreeBoardPostRequestDto;
import com.joyopi.backend.freeboard.dto.FreeBoardPostResponseDto;
import com.joyopi.backend.freeboard.repository.FreeBoardPostEntity;
import com.joyopi.backend.freeboard.repository.FreeBoardPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreeBoardPostService {

    private final FreeBoardPostRepository freeBoardPostRepository;
    private final FreeBoardPostMapper freeBoardPostMapper;

    // 페이징 방식으로 게시글 조회
    public Page<FreeBoardPostResponseDto> getPostsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FreeBoardPostEntity> postEntities = freeBoardPostRepository.findAll(pageable);

        // Entity → Domain → ResponseDTO 변환
        return postEntities
                .map(freeBoardPostMapper::toPost)
                .map(freeBoardPostMapper::toPostResponseDTO);
    }

    // 무한스크롤 방식으로 게시글 조회
    public List<FreeBoardPostResponseDto> getPostsWithInfiniteScroll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        List<FreeBoardPostEntity> postEntities = freeBoardPostRepository.findAll(pageable).getContent();

        // Entity → Domain → ResponseDTO 변환
        return postEntities.stream()
                .map(freeBoardPostMapper::toPost)
                .map(freeBoardPostMapper::toPostResponseDTO)
                .collect(Collectors.toList());
    }

    // 게시글 작성
    public FreeBoardPostResponseDto createPost(FreeBoardPostRequestDto requestDto) {
        // RequestDTO → Domain → Entity 변환 및 저장
        FreeBoardPost freeBoardPost = freeBoardPostMapper.toPost(requestDto);
        FreeBoardPostEntity postEntity = freeBoardPostMapper.toPostEntity(freeBoardPost);
        FreeBoardPostEntity savedEntity = freeBoardPostRepository.save(postEntity);

        // 저장된 Entity → Domain → ResponseDTO 변환
        FreeBoardPost savedPost = freeBoardPostMapper.toPost(savedEntity);
        return freeBoardPostMapper.toPostResponseDTO(savedPost);
    }

    // 게시글 수정
    public FreeBoardPostResponseDto updatePost(Long id, Long userId, FreeBoardPostRequestDto requestDto) {
        // 게시글이 존재하는지 확인
        FreeBoardPostEntity freeBoardPostEntity = freeBoardPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 게시글 작성자와 로그인한 사용자가 일치하는지 확인
        if (!freeBoardPostEntity.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("이 게시글을 수정할 권한이 없습니다.");
        }

        // 기존 게시글의 데이터를 수정하기
        freeBoardPostEntity.setTitle(requestDto.getTitle()); // 예시: 제목 수정
        freeBoardPostEntity.setContent(requestDto.getContent()); // 예시: 내용 수정

        // 수정된 Entity 저장
        FreeBoardPostEntity updatedEntity = freeBoardPostRepository.save(freeBoardPostEntity);

        // 저장된 Entity → Domain → ResponseDTO 변환
        FreeBoardPost updatedPost = freeBoardPostMapper.toPost(updatedEntity);
        return freeBoardPostMapper.toPostResponseDTO(updatedPost);
    }


    // 게시글 삭제
    public void deletePost(Long id) {
        freeBoardPostRepository.deleteById(id);
    }
}
