package com.joyopi.backend.freeboard.controller;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.service.FreeBoardPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freeboard/posts")
public class FreeBoardPostController {

    private final FreeBoardPostService freeBoardPostService;

    @Autowired
    public FreeBoardPostController(FreeBoardPostService freeBoardPostService) {
        this.freeBoardPostService = freeBoardPostService;
    }

    // 페이징 방식으로 게시글 조회
    @GetMapping("/page")
    public ResponseEntity<Page<FreeBoardPost>> getPostsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FreeBoardPost> posts = freeBoardPostService.getPostsWithPaging(page, size);
        return ResponseEntity.ok(posts);
    }

    // 무한스크롤 방식으로 게시글 조회
    @GetMapping("/infinite")
    public ResponseEntity<List<FreeBoardPost>> getPostsInfinite(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<FreeBoardPost> freeBoardPosts = freeBoardPostService.getPostsWithInfiniteScroll(page, size);
        return ResponseEntity.ok(freeBoardPosts);
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<FreeBoardPost> createPost(@RequestBody FreeBoardPost freeBoardPost) {
        FreeBoardPost createdFreeBoardPost = freeBoardPostService.createPost(freeBoardPost);
        return ResponseEntity.status(201).body(createdFreeBoardPost);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<FreeBoardPost> updatePost(
            @PathVariable Long id,
            @RequestBody FreeBoardPost freeBoardPost) {
        FreeBoardPost updatedFreeBoardPost = freeBoardPostService.updatePost(id, freeBoardPost);
        return ResponseEntity.ok(updatedFreeBoardPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        freeBoardPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}