package com.joyopi.backend.freeboard.repository;

import com.joyopi.backend.user.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FreeBoardPostRepositoryTest {

    @Autowired
    private FreeBoardPostRepository freeBoardPostRepository;

    // 게시글 100개를 테스트 데이터로 삽입하는 테스트
    @Test
    @Transactional // 자동으로 롤백시킴
    @Rollback(false) // 자동 롤백 하지 않음
    public void testInsertFreeBoardPosts() {
        // 랜덤 데이터를 생성하여 100개의 게시글 삽입
        for (int i = 1; i <= 100; i++) {
            UserEntity user = new UserEntity();
            user.setId(1L);
            FreeBoardPostEntity post = new FreeBoardPostEntity();
            post.setTitle("Test Title " + i);
            post.setContent("This is the content of post " + i);
            post.setUser(user); // 생성한 사용자와 연관

            freeBoardPostRepository.save(post);
        }

        // 저장된 게시글이 100개인지 확인
        List<FreeBoardPostEntity> posts = freeBoardPostRepository.findAll();
        assertThat(posts).hasSizeGreaterThanOrEqualTo(100); // 100개의 게시글이 저장되어야 함
    }
}