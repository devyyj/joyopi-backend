package com.joyopi.backend.freeboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreeBoardPostRepository extends JpaRepository<FreeBoardPostEntity, Long> {

    // 페이징 방식으로 게시글 목록 조회
    Page<FreeBoardPostEntity> findAll(Pageable pageable);
}