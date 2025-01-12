package com.joyopi.backend.freeboard.service;

import com.joyopi.backend.freeboard.domain.FreeBoardPost;
import com.joyopi.backend.freeboard.repository.FreeBoardPostEntity;
import com.joyopi.backend.freeboard.repository.FreeBoardPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreeBoardPostService {

    private final FreeBoardPostRepository freeBoardPostRepository;
    private final FreeBoardPostMapper freeBoardPostMapper;

    @Autowired
    public FreeBoardPostService(FreeBoardPostRepository freeBoardPostRepository, FreeBoardPostMapper freeBoardPostMapper) {
        this.freeBoardPostRepository = freeBoardPostRepository;
        this.freeBoardPostMapper = freeBoardPostMapper;
    }

    // 페이징 방식으로 게시글 조회
    public Page<FreeBoardPost> getPostsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FreeBoardPostEntity> postEntities = freeBoardPostRepository.findAll(pageable);
        return postEntities.map(freeBoardPostMapper::toPost); // PostEntity -> Post 변환
    }

    // 무한스크롤 방식으로 게시글 조회
    public List<FreeBoardPost> getPostsWithInfiniteScroll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<FreeBoardPostEntity> postEntities = freeBoardPostRepository.findAll(pageable).getContent();
        return freeBoardPostMapper.toPost(postEntities); // List<PostEntity> -> List<Post> 변환
    }

    // 게시글 작성
    public FreeBoardPost createPost(FreeBoardPost freeBoardPost) {
        FreeBoardPostEntity freeBoardPostEntity = freeBoardPostMapper.toPostEntity(freeBoardPost);
        FreeBoardPostEntity savedPost = freeBoardPostRepository.save(freeBoardPostEntity);
        return freeBoardPostMapper.toPost(savedPost);
    }

    // 게시글 수정
    public FreeBoardPost updatePost(Long id, FreeBoardPost freeBoardPost) {
        if (!freeBoardPostRepository.existsById(id)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        freeBoardPost.setId(id);
        FreeBoardPostEntity freeBoardPostEntity = freeBoardPostMapper.toPostEntity(freeBoardPost);
        FreeBoardPostEntity updatedPost = freeBoardPostRepository.save(freeBoardPostEntity);
        return freeBoardPostMapper.toPost(updatedPost);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        freeBoardPostRepository.deleteById(id);
    }
}