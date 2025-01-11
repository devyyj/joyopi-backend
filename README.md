# 사용자 인증

## 로그인 - OAuth

### OAuth 공급자

1. 카카오

### JWT 발급 - 서버

- 액세스 토큰과 리프레시 토큰을 발급
- 액세스 토큰은 본문으로 응답
- 리프레시 토큰은 쿠키 설정으로 응답 (HttpOnly 및 Secure 설정)

### JWT 사용 - 클라이언트

- 서버로부터 응답받은 액세스 토큰을 안전한 곳에 저장
    - 메모리(로컬 변수)에 저장하는 것을 추천
- 인증정보가 필요한 API를 호출할 때 아래와 같이 요청 헤더를 설정
    - Authorization: Bearer <액세스 토큰>
- 리프레시 토큰은 브라우저 쿠키에 저장되므로 자동으로 모든 요청에 포함됨

### JWT 만료 및 재발급

- 액세스 토큰이 만료됐을 경우 API 호출시 서버는 HTTP Error Code 401을 응답
- 클라이언트는 401을 응답 받았을 경우 아래 API를 호출하여 액세스 토큰 재발급
    - /auth/refresh-token
- 클라이언트는 액세스 토큰을 재발급 받은 뒤에 처음 호출하려고 했던 API를 다시 호출 하도록 설정하는 것을 추천
- 리프레시 토큰도 만료 됐을 경우 서버는 401 Error Code를 응답
    - 이때, 클라이언트는 사용자를 로그아웃 시키고 다시 로그인할 수 있도록 유도

## 로그아웃

### 조건

- 같은 OAuth 공급자의 다른 계정으로 로그인 가능해야 함
    - 카카오 로그인 (A 계정) -> 로그아웃 -> 카카오 로그인 (B 계정)
- 다른 OAuth 공급자로 로그인 가능해야 함
    - 네이버 로그인 -> 로그아웃 -> 구글 로그인

### 기능

- OAuth 공급자 서버 로그아웃
    - 로그인할 때 소셜 정보를 다시 입력
- 쿠키로 저장된 리프레시 토큰 삭제
- 액세스 토큰은 클라이언트 측에서 삭제해야 함 (서버측에서 제어 불가능)

## 탈퇴

- 서비스 DB에서 사용자 정보를 삭제
- 소셜(OAuth 공급자)과 서비스의 연결을 끊음
    - 다시 로그인(가입)할 경우 해당 서비스에 연결(가입)한다는 메시지가 생성됨

# 예외 처리

# 로깅