package com.example.springbootboilerplate.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    @Mock
    private JpaUserRepository jpaUserRepository;  // JpaUserRepository 모킹

    @InjectMocks
    private UserRepositoryImpl userRepositoryImpl;  // 테스트할 클래스

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // UserEntity 객체 생성
        user = new User();
        user.setId(1L);
        user.setOauthProvider("google");
        user.setOauthId("google-oauth-id-123");
        user.setRoles("USER");
    }

    @Test
    void testFindAll() {
        // Given
        List<User> userList = List.of(user);
        when(jpaUserRepository.findAll()).thenReturn(userList);

        // When
        List<User> result = userRepositoryImpl.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("google", result.getFirst().getOauthProvider());
        assertEquals("google-oauth-id-123", result.getFirst().getOauthId());
        assertEquals("USER", result.getFirst().getRoles());
        verify(jpaUserRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userRepositoryImpl.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("google", result.get().getOauthProvider());
        assertEquals("google-oauth-id-123", result.get().getOauthId());
        assertEquals("USER", result.get().getRoles());
        verify(jpaUserRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByOauthProviderAndOauthId() {
        // Given
        when(jpaUserRepository.findByOauthProviderAndOauthId("google", "google-oauth-id-123"))
                .thenReturn(Optional.of(user));

        // When
        Optional<User> result = userRepositoryImpl.findByOauthProviderAndOauthId("google", "google-oauth-id-123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("google", result.get().getOauthProvider());
        assertEquals("google-oauth-id-123", result.get().getOauthId());
        assertEquals("USER", result.get().getRoles());
        verify(jpaUserRepository, times(1)).findByOauthProviderAndOauthId("google", "google-oauth-id-123");
    }

    @Test
    void testSave() {
        // Given
        when(jpaUserRepository.save(user)).thenReturn(user);  // save 메서드가 userEntity를 반환하도록 설정

        // When
        User result = userRepositoryImpl.save(user);  // save() 메서드 호출

        // Then
        assertNotNull(result);  // 반환된 결과가 null이 아닌지 확인
        assertEquals(user.getId(), result.getId());  // 반환된 userEntity의 id가 입력과 같은지 확인
        assertEquals(user.getOauthProvider(), result.getOauthProvider());  // oauthProvider가 동일한지 확인
        assertEquals(user.getOauthId(), result.getOauthId());  // oauthId가 동일한지 확인
        assertEquals(user.getRoles(), result.getRoles());  // roles가 동일한지 확인

        verify(jpaUserRepository, times(1)).save(user);  // save()가 정확히 한번 호출되었는지 확인
    }

    @Test
    void testDeleteById() {
        // Given
        doNothing().when(jpaUserRepository).deleteById(1L);

        // When
        userRepositoryImpl.deleteById(1L);

        // Then
        verify(jpaUserRepository, times(1)).deleteById(1L);
    }
}