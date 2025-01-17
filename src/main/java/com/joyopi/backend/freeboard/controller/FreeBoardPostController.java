package com.joyopi.backend.freeboard.controller;

import com.joyopi.backend.freeboard.dto.FreeBoardPostRequestDto;
import com.joyopi.backend.freeboard.dto.FreeBoardPostResponseDto;
import com.joyopi.backend.freeboard.service.FreeBoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freeboard/posts")
@RequiredArgsConstructor
public class FreeBoardPostController {

    private final FreeBoardPostService freeBoardPostService;

    // 페이징 방식으로 게시글 조회
    @GetMapping("/page")
    public ResponseEntity<Page<FreeBoardPostResponseDto>> getPostsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FreeBoardPostResponseDto> responseDTOs = freeBoardPostService.getPostsWithPaging(page, size);
        return ResponseEntity.ok(responseDTOs);
    }

    // 무한스크롤 방식으로 게시글 조회
    @GetMapping("/infinite")
    public ResponseEntity<List<FreeBoardPostResponseDto>> getPostsInfinite(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<FreeBoardPostResponseDto> responseDTOs = freeBoardPostService.getPostsWithInfiniteScroll(page, size);
        return ResponseEntity.ok(responseDTOs);
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<FreeBoardPostResponseDto> createPost(
            @AuthenticationPrincipal Long userId,
            @RequestBody FreeBoardPostRequestDto requestDto) {
        requestDto.setUserId(userId);
        FreeBoardPostResponseDto responseDTO = freeBoardPostService.createPost(requestDto);
        return ResponseEntity.status(201).body(responseDTO);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<FreeBoardPostResponseDto> updatePost(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId,
            @RequestBody FreeBoardPostRequestDto requestDto) {
        FreeBoardPostResponseDto responseDTO = freeBoardPostService.updatePost(id, userId, requestDto);
        return ResponseEntity.ok(responseDTO);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        freeBoardPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
