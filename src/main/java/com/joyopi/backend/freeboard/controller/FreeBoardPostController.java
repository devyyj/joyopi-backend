package com.joyopi.backend.freeboard.controller;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.dto.FreeBoardPostRequestDto;
import com.joyopi.backend.freeboard.dto.FreeBoardPostResponseDto;
import com.joyopi.backend.freeboard.service.FreeBoardPostMapper;
import com.joyopi.backend.freeboard.service.FreeBoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/freeboard/posts")
@RequiredArgsConstructor
public class FreeBoardPostController {

    private final FreeBoardPostService freeBoardPostService;
    private final FreeBoardPostMapper freeBoardPostMapper;

    // 페이징 방식으로 게시글 조회
    @GetMapping("/page")
    public ResponseEntity<Page<FreeBoardPostResponseDto>> getPostsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FreeBoardPost> posts = freeBoardPostService.getPostsWithPaging(page, size);
        Page<FreeBoardPostResponseDto> responseDTOs = posts.map(freeBoardPostMapper::toPostResponseDTO);
        return ResponseEntity.ok(responseDTOs);
    }

    // 무한스크롤 방식으로 게시글 조회
    @GetMapping("/infinite")
    public ResponseEntity<List<FreeBoardPostResponseDto>> getPostsInfinite(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<FreeBoardPost> freeBoardPosts = freeBoardPostService.getPostsWithInfiniteScroll(page, size);
        List<FreeBoardPostResponseDto> responseDTOs = freeBoardPosts.stream()
                .map(freeBoardPostMapper::toPostResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<FreeBoardPostResponseDto> createPost(@AuthenticationPrincipal String userId, @RequestBody FreeBoardPostRequestDto requestDto) {
        requestDto.setUserId(Long.parseLong(userId));
        FreeBoardPost freeBoardPost = freeBoardPostService.createPost(requestDto);
        FreeBoardPostResponseDto responseDTO = freeBoardPostMapper.toPostResponseDTO(freeBoardPost);
        return ResponseEntity.status(201).body(responseDTO);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<FreeBoardPostResponseDto> updatePost(
            @PathVariable Long id,
            @RequestBody FreeBoardPostRequestDto freeBoardPostRequestDTO) {
        FreeBoardPost freeBoardPost = freeBoardPostService.updatePost(id, freeBoardPostRequestDTO);
        FreeBoardPostResponseDto responseDTO = freeBoardPostMapper.toPostResponseDTO(freeBoardPost);
        return ResponseEntity.ok(responseDTO);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        freeBoardPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
